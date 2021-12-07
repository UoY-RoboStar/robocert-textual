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
package robocalc.robocert.generator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
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

import com.google.inject.Inject;
import com.google.inject.Provider;

import robocalc.robocert.RoboCertStandaloneSetup;
import robocalc.robocert.generator.utils.FilenameExtensions;

/**
 * Command-line interface for the RoboCert generator.
 *
 * While this is currently parallel to the RoboChart CSP generator, this may
 * change later on.
 *
 * @author Matt Windsor
 */
public class Main {
	/**
	 * Runs the command-line interface.
	 *
	 * @param args the arguments (only one, the path to the project, should be
	 *             given).
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: robocert PATH_TO_ROBOCHART_PROJECT");
			System.exit(2);
		}
		var injector = new RoboCertStandaloneSetup().createInjectorAndDoEMFRegistration();
		
		var main = injector.getInstance(Main.class);
		System.exit(main.runGenerator(args[0]));
	}

	@Inject
	private Provider<ResourceSet> resourceSetProvider;

	@Inject
	private IResourceValidator validator;

	@Inject
	private GeneratorDelegate generator;

	@Inject
	private JavaIoFileSystemAccess fileAccess;

	@Inject
	private FilenameExtensions filename;

	@Inject
	private IOutputConfigurationProvider outputConfigurationProvider;

	/**
	 * Runs the generator.
	 *
	 * @param string unparsed path to the project to generate.
	 *
	 * @return exit code (0 for success, 1 for failure).
	 */
	protected int runGenerator(String string) {
		setupOutputConfigurations(string);

		var project = FileSystems.getDefault().getPath(string);
		if (!Files.isDirectory(project)) {
			System.err.println("%s is not a path to a RoboChart project".formatted(project.toString()));
			return 1;
		}

		List<Path> paths;
		try {
			paths = findFiles(project);
		} catch (IOException e) {
			System.err.println("I/O error while reading project %s".formatted(project.toString()));
			e.printStackTrace();
			return 1;
		}

		ResourceSet set;
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

		var resources = loadResources(set, paths);

		var numErrors = validateResources(resources);
		if (0 < numErrors) {
			System.err.println("%d validation error(s), stopping.".formatted(numErrors));
			return 1;
		}

		generateResources(resources);
		return 0;
	}

	//
	// Output configuration
	//

	private void setupOutputConfigurations(String string) {
		fileAccess.setOutputConfigurations(outputConfigurationProvider.getOutputConfigurations().parallelStream()
				.peek(x -> expandRelativeDir(string, x))
				.collect(Collectors.toUnmodifiableMap(OutputConfiguration::getName, x -> x)));
	}

	private void expandRelativeDir(String string, OutputConfiguration x) {
		x.setOutputDirectory(x.getOutputDirectory().replaceFirst("^.", string));
	}

	//
	// File discovery
	//

	private List<Path> findFiles(Path project) throws IOException {
		return Files.walk(project).filter(this::shouldConsider).toList();
	}

	private boolean shouldConsider(Path p) {
		// NOTE(@MattWindsor91): we generate CSP for RoboChart here too.
		// Should we?
		var isFile = Files.isRegularFile(p);
		return isFile && (filename.isRoboChartFile(p) || filename.isRoboCertFile(p));
	}

	//
	// Resource loading
	//

	private List<Resource> loadResources(ResourceSet set, List<Path> paths) {
		var resources = paths.stream().map(p -> loadResource(set, p)).peek(this::printResource).toList();
		return resources;
	}

	private static Resource loadResource(ResourceSet set, Path p) {
		return set.getResource(URI.createFileURI(p.toString()), true);
	}

	private void printResource(Resource r) {
		System.out.println("Loaded: %s".formatted(r.getURI().toString()));
	}

	//
	// RoboChart library loading
	//

	private ResourceSet baseResourceSet() throws URISyntaxException, IOException {
		// This is taken primarily from the RoboChart CSP generator.
		// TODO(@MattWindsor91): unify this and upstream?

		var set = resourceSetProvider.get();
		var uri = findRoboChartStandardLibrary();

		Path myPath;
		FileSystem fs = null;
		if ("jar".equals(uri.getScheme())) {
			fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
			myPath = fs.getPath("lib/robochart");
		} else
			myPath = Paths.get(uri);

		// can't use the stream directly because addResourceToSet throws
		var walk = Files.list(myPath);
		for (var it = walk.iterator(); it.hasNext();)
			addResourceToSet(set, it.next());
		walk.close();

		if (fs != null)
			fs.close();
		return set;
	}

	private java.net.URI findRoboChartStandardLibrary() throws URISyntaxException {
		var classLoader = RoboCertStandaloneSetup.class.getClassLoader();
		var url = classLoader.getResource("lib/robochart");
		if (url == null)
			url = classLoader.getResource("robochart");
		var uri = url.toURI();
		System.out.println(uri);
		return uri;
	}

	private void addResourceToSet(ResourceSet set, Path p) throws IOException {
		var is = Files.newInputStream(p);
		var furi = URI.createFileURI(p.toString());
		var r = set.createResource(furi);
		r.load(is, set.getLoadOptions());
		System.out.println("Loaded: " + p);
	}

	//
	// Validation
	//

	private long validateResources(List<Resource> resources) {
		return resources.stream().flatMap(this::validate).filter(this::isError).peek(this::printValidationError)
				.count();
	}

	private Stream<Issue> validate(Resource r) {
		return validator.validate(r, CheckMode.ALL, CancelIndicator.NullImpl).stream();
	}

	private boolean isError(Issue i) {
		return i.getSeverity() == Severity.ERROR;
	}

	private void printValidationError(Issue i) {
		System.err.println("Error: %s".formatted(i));
	}

	//
	// Generation
	//

	private void generateResources(List<Resource> resources) {
		var context = new GeneratorContext();
		context.setCancelIndicator(CancelIndicator.NullImpl);
		resources.stream().peek(this::printGenerate).forEach(r -> generate(r, context));
	}

	private void printGenerate(Resource r) {
		System.out.println("Generating %s".formatted(r.getURI()));
	}

	private void generate(Resource r, GeneratorContext ctx) {
		generator.doGenerate(r, fileAccess, ctx);
	}
}
