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
 * Uses a formal parameter of an operation as a target parameter.
 * @param param the parameter to wrap.
 *
 * @author Matt Windsor
 */
public record FormalParameter(circus.robocalc.robochart.Parameter param) implements Parameter {
  @Override
  public String prefix() {
    return "param";
  }

  @Override
  public QualifiedName qualifiedName(IQualifiedNameProvider qnp) {
    return QualifiedName.create(param.getName());
  }

  @Override
  public Optional<Variable> tryGetConstant() {
    return Optional.empty();
  }
}
