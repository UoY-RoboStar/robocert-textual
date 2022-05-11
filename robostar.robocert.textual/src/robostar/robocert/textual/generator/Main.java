/********************************************************************************
 * Copyright (c) 2019-2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alvaro Miyazawa - original code in RoboChart CSP generator
 *   Pedro Ribeiro - original code in RoboChart CSP Generator
 *   Matt Windsor - initial definition in RoboCert
 ********************************************************************************/
package robostar.robocert.textual.generator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import robostar.robocert.textual.RoboCertStandaloneSetup;
import robostar.robocert.textual.generator.utils.FilenameHelper;

/**
 * Command-line interface for the RoboCert generator.
 *
 * <p>While this is currently parallel to the RoboChart CSP generator, this may change later on.
 *
 * @author Matt Windsor
 */
public class Main {

  @Inject
  private Provider<ResourceSet> resourceSetProvider;
  @Inject
  private IResourceValidator validator;
  @Inject
  private GeneratorDelegate generator;
  @Inject
  private JavaIoFileSystemAccess fileAccess;
  @Inject
  private FilenameHelper filename;
  @Inject
  private IOutputConfigurationProvider outputConfigurationProvider;

  /**
   * Runs the command-line interface.
   *
   * @param args the arguments (only one, the path to the project, should be given).
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: robocert PATH_TO_ROBOCHART_PROJECT");
      System.exit(2);
    }
    final var injector = new RoboCertStandaloneSetup().createInjectorAndDoEMFRegistration();

    final var main = injector.getInstance(Main.class);
    System.exit(main.runGenerator(args[0]));
  }

  private static Resource loadResource(ResourceSet set, Path p) {
    return set.getResource(URI.createFileURI(p.toString()), true);
  }

  //
  // Output configuration
  //

  /**
   * Runs the generator.
   *
   * @param string unparsed path to the project to generate.
   * @return exit code (0 for success, 1 for failure).
   */
  protected int runGenerator(String string) {
    setupOutputConfigurations(string);

    final var project = FileSystems.getDefault().getPath(string);
    if (!Files.isDirectory(project)) {
      System.err.printf("%s is not a path to a RoboChart project%n", project);
      return 1;
    }

    final List<Path> paths;
    try {
      paths = findFiles(project);
    } catch (IOException e) {
      System.err.printf("I/O error while reading project %s%n", project);
      e.printStackTrace();
      return 1;
    }

    final ResourceSet set;
    try {
      set = baseResourceSet();
    } catch (IOException e) {
      System.err.println("I/O error while getting RoboChart resource set");
      e.printStackTrace();
      return 1;
    } catch (URISyntaxException e) {
      System.err.println("URI syntax error while getting RoboChart resource set");
      e.printStackTrace();
      return 1;
    }

    final var resources = loadResources(set, paths);

    final var numErrors = validateResources(resources);
    if (0 < numErrors) {
      System.err.printf("%d validation error(s), stopping.%n", numErrors);
      return 1;
    }

    generateResources(resources);
    return 0;
  }

  private void setupOutputConfigurations(String string) {
    fileAccess.setOutputConfigurations(
        outputConfigurationProvider.getOutputConfigurations().parallelStream()
            .peek(x -> expandRelativeDir(string, x))
            .collect(Collectors.toUnmodifiableMap(OutputConfiguration::getName, x -> x)));
  }

  //
  // File discovery
  //

  private void expandRelativeDir(String string, OutputConfiguration x) {
    x.setOutputDirectory(x.getOutputDirectory().replaceFirst("^.", string));
  }

  @SuppressWarnings("LocalCanBeFinal")
  private List<Path> findFiles(Path project) throws IOException {
    try (var files = Files.walk(project)) {
      return files.filter(this::shouldConsider).toList();
    }
  }

  //
  // Resource loading
  //

  private boolean shouldConsider(Path p) {
    // NOTE(@MattWindsor91): we generate CSP for RoboChart here too.
    // Should we?
    final var isFile = Files.isRegularFile(p);
    return isFile && (filename.isRoboChartFile(p) || filename.isRoboCertFile(p));
  }

  private List<Resource> loadResources(ResourceSet set, List<Path> paths) {
    return paths.stream().map(p -> loadResource(set, p)).peek(this::printResource).toList();
  }

  private void printResource(Resource r) {
    System.out.printf("Loaded: %s%n", r.getURI().toString());
  }

  //
  // RoboChart library loading
  //

  private ResourceSet baseResourceSet() throws URISyntaxException, IOException {
    // This is taken primarily from the RoboChart CSP generator.
    // TODO(@MattWindsor91): unify this and upstream?

    final var set = resourceSetProvider.get();
    final var uri = findRoboChartStandardLibrary();

    final Path myPath;
    FileSystem fs = null;
    if ("jar".equals(uri.getScheme())) {
      fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
      myPath = fs.getPath("lib/robochart");
    } else {
      myPath = Paths.get(uri);
    }

    // can't use the stream directly because addResourceToSet throws
    //noinspection LocalCanBeFinal
    try (var walk = Files.list(myPath)) {
      for (final var it = walk.iterator(); it.hasNext(); ) {
        addResourceToSet(set, it.next());
      }
    }

    if (fs != null) {
      fs.close();
    }
    return set;
  }

  private java.net.URI findRoboChartStandardLibrary() throws URISyntaxException {
    final var classLoader = RoboCertStandaloneSetup.class.getClassLoader();
    var url = classLoader.getResource("lib/robochart");
    if (url == null) {
      url = classLoader.getResource("robochart");
    }
    final var uri = Objects.requireNonNull(url).toURI();
    System.out.println(uri);
    return uri;
  }

  private void addResourceToSet(ResourceSet set, Path p) throws IOException {
    final var is = Files.newInputStream(p);
    final var furi = URI.createFileURI(p.toString());
    final var r = set.createResource(furi);
    r.load(is, set.getLoadOptions());
    System.out.println("Loaded: " + p);
  }

  //
  // Validation
  //

  private long validateResources(List<Resource> resources) {
    return resources.stream()
        .flatMap(this::validate)
        .filter(this::isError)
        .peek(this::printValidationError)
        .count();
  }

  private Stream<Issue> validate(Resource r) {
    return validator.validate(r, CheckMode.ALL, CancelIndicator.NullImpl).stream();
  }

  private boolean isError(Issue i) {
    return i.getSeverity() == Severity.ERROR;
  }

  private void printValidationError(Issue i) {
    System.err.printf("Error: %s%n", i);
  }

  //
  // Generation
  //

  private void generateResources(List<Resource> resources) {
    final var context = new GeneratorContext();
    context.setCancelIndicator(CancelIndicator.NullImpl);
    resources.stream().peek(this::printGenerate).forEach(r -> generate(r, context));
  }

  private void printGenerate(Resource r) {
    System.out.printf("Generating %s%n", r.getURI());
  }

  private void generate(Resource r, GeneratorContext ctx) {
    generator.doGenerate(r, fileAccess, ctx);
  }
}
