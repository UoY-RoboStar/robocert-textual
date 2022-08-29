/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.utils;

import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.CertPackage;

/**
 * Basic skeleton of a generator that emits files, or groups of files, for each {@link CertPackage}
 * in a resource.
 *
 * @author Matt Windsor
 */
public interface PackageGenerator {

  /**
   * Generates for a package.
   *
   * @param fsa     filesystem access.
   * @param context context for checking if the action has been cancelled.
   * @param pkg     package to generate.
   */
  void generate(IFileSystemAccess2 fsa, IGeneratorContext context, CertPackage pkg);
}
