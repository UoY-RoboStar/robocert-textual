/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.plugin;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.textual.generator.utils.GeneratorUtil;
import robostar.robocert.textual.generator.utils.PackageGenerator;
import robostar.robocert.textual.generator.utils.StandardLibraryGenerator;

/**
 * Abstractly implements the RoboCert style of generator.
 * <p>
 *
 * This involves copying over a standard library of (CSP, TikZ, etc) definitions, then generating
 * separately for each package.
 *
 * @author Matt Windsor
 */
public abstract class AbstractRoboCertGeneratorPlugin extends AbstractGenerator implements RoboCertGeneratorPlugin {

  @Override
  public void doGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
    final var libGen = libGenerator();
    final var isCancelled = libGen.generate(fsa, context, getClass());
    if (isCancelled) {
      // Don't bother generating the packages.
      return;
    }

    final var pkgGen = pkgGenerator();
    GeneratorUtil.forEachPackage(input, context, pkg -> pkgGen.generate(fsa, context, pkg));
  }

  /**
   * Factory method for the package generator.
   * @return a generator mapping RoboCert packages to output.
   */
  protected abstract PackageGenerator pkgGenerator();

  /**
   * Factory method for the library generator.
   * @return a library generator, which should be configured in-line here.
   */
  protected abstract StandardLibraryGenerator libGenerator();

}
