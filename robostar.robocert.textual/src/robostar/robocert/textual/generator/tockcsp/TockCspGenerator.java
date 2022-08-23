/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/
package robostar.robocert.textual.generator.tockcsp;

import com.google.inject.Inject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.textual.generator.RoboCertOutputConfigurationProvider;
import robostar.robocert.textual.generator.utils.GeneratorUtil;
import robostar.robocert.textual.generator.utils.name.GroupNamer;

/**
 * Generates tock-CSP from RoboCert specifications.
 *
 * @author Matt Windsor
 */
public class TockCspGenerator extends AbstractGenerator {

  private final CertPackageGenerator csp;
  private final GroupNamer gn;

  /**
   * Constructs a tock-CSP generator.
   *
   * @param csp generator for CertPackage file content.
   * @param gn  synthesises names for CertPackages.
   */
  @Inject
  public TockCspGenerator(CertPackageGenerator csp, GroupNamer gn) {
    super();

    this.csp = csp;
    this.gn = gn;
  }

  @Override
  public void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
    final var isCanceled = generateCSPStandardLibrary(fsa, context);
    if (isCanceled) {
      return;
    }

    generateCSPPackages(resource, fsa, context);
  }

  private boolean generateCSPStandardLibrary(IFileSystemAccess2 fsa, IGeneratorContext context) {
    for (var filename : CSP_LIBRARY_FILES) {
      generateCSPStandardLibraryFile(fsa, filename);
      if (context.getCancelIndicator().isCanceled()) {
        return true;
      }
    }
    return false;
  }

  private void generateCSPStandardLibraryFile(IFileSystemAccess2 fsa, String filename) {
    final var stream = TockCspGenerator.class.getResourceAsStream("lib/semantics/" + filename);
    fsa.generateFile(filename, RoboCertOutputConfigurationProvider.CSP_LIBRARY_OUTPUT, stream);
  }

  private void generateCSPPackages(Resource resource, IFileSystemAccess2 fsa,
      IGeneratorContext context) {
    GeneratorUtil.forEachPackage(resource, context, x -> {
      // TODO(@MattWindsor91): multiple packages in one resource?
      fsa.generateFile(gn.getPackageName(x) + ".csp", csp.generate(x));
    });
  }

  private static final String[] CSP_LIBRARY_FILES = new String[]{"robocert_defs.csp",
      "robocert_seq_defs.csp"};
}
