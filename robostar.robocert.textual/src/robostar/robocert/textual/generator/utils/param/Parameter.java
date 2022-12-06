/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.utils.param;

import circus.robocalc.robochart.Variable;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import java.util.Optional;

/**
 * Common denominator of functionality of things that make up a target parameterisation.
 *
 * @author Matt Windsor
 */
public interface Parameter {
  /**
   * Gets the namespace into which this type of parameter should be placed in CSP etc.
   *
   * @return the prefix to put on the qualified name.
   */
  String prefix();

  /**
   * Gets the qualified name of the parameter (as used in parameterisation files, etc).
   *
   * @param qnp a qualified name provider.
   * @return the qualified name of the parameter.
   */
  QualifiedName qualifiedName(IQualifiedNameProvider qnp);

  /**
   * @return the underlying constant of this parameter, if one exists.
   */
  Optional<Variable> tryGetConstant();
}
