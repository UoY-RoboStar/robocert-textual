/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz;


import com.google.inject.Inject;

import robostar.robocert.textual.generator.RoboCertOutputConfigurationProvider;
import robostar.robocert.textual.generator.utils.PackageGenerator;
import robostar.robocert.textual.generator.utils.StandardLibraryGenerator;
import robostar.robocert.textual.generator.utils.param.AbstractRoboCertGenerator;

/**
 * Generates TikZ diagrams from RoboCert interaction diagrams.
 *
 * @author Matt Windsor
 */
public class TikzGenerator extends AbstractRoboCertGenerator {

  private final StandardLibraryGenerator libGen;
  private final CertPackageGenerator pkgGen;

  /**
   * Constructs a TikZ generator.
   *
   * @param libGen copies the TikZ standard definitions to the output directory.
   * @param pkgGen generates TikZ for the packages in a resource.
   */
  @Inject
  public TikzGenerator(StandardLibraryGenerator libGen, CertPackageGenerator pkgGen) {
    super();

    libGen.setOutputConfiguration(RoboCertOutputConfigurationProvider.TIKZ_LIBRARY_OUTPUT);
    libGen.setInputDirectory("lib");
    libGen.addFiles("defs.tex");

    this.libGen = libGen;
    this.pkgGen = pkgGen;
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
