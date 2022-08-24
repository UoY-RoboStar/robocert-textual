/*
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
 */
package robostar.robocert.textual.generator.tockcsp;

import com.google.inject.Inject;
import robostar.robocert.textual.generator.RoboCertOutputConfigurationProvider;
import robostar.robocert.textual.generator.utils.PackageGenerator;
import robostar.robocert.textual.generator.utils.StandardLibraryGenerator;
import robostar.robocert.textual.generator.utils.param.AbstractRoboCertGenerator;

/**
 * Generates tock-CSP from RoboCert specifications.
 *
 * @author Matt Windsor
 */
public class TockCspGenerator extends AbstractRoboCertGenerator {

  private final CertPackageGenerator pkgGen;
  private final StandardLibraryGenerator libGen;

  /**
   * Constructs a tock-CSP generator.
   *
   * @param pkgGen generator for CertPackage file content.
   * @param libGen generator for the tock-CSP standard library.
   */
  @Inject
  public TockCspGenerator(CertPackageGenerator pkgGen, StandardLibraryGenerator libGen) {
    super();

    libGen.setOutputConfiguration(RoboCertOutputConfigurationProvider.CSP_LIBRARY_OUTPUT);
    libGen.setInputDirectory("lib/semantics");
    libGen.addFiles("robocert_defs.csp", "robocert_seq_defs.csp");

    this.pkgGen = pkgGen;
    this.libGen = libGen;
  }

  @Override
  protected PackageGenerator pkgGenerator() {
    return pkgGen;
  }

  @Override
  protected StandardLibraryGenerator libGenerator() {
    return libGen;
  }
}
