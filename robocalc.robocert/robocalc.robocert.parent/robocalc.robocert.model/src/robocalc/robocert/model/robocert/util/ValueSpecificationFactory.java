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

package robocalc.robocert.model.robocert.util;

import com.google.inject.Inject;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.ExpressionValueSpecification;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.WildcardValueSpecification;

/**
 * High-level factory for creating value specifications (arguments).
 *
 * @author Matt Windsor
 */
public record ValueSpecificationFactory(ExpressionFactory exprFactory, RoboCertFactory rcertFactory) {

  /**
   * Constructs a value specification factory.
   * @param exprFactory the expression factory to which we delegate.
   * @param rcertFactory the low-level RoboCert factory to which we delegate.
   */
  @Inject
  public ValueSpecificationFactory {}

  /**
   * Constructs a value specification for the given integer.
   *
   * @param v the integer to lift to a value specification.
   * @return the given integer value specification.
   */
  public ExpressionValueSpecification integer(int v) {
    final var spec = rcertFactory.createExpressionValueSpecification();
    spec.setExpr(exprFactory.integer(v));
    return spec;
  }

  /**
   * Constructs an unbound wildcard value specification.
   *
   * @return  a wildcard value specification.
   */
  public WildcardValueSpecification wildcard() {
    return rcertFactory.createWildcardValueSpecification();
  }

  /**
   * Constructs a bound value specification.
   *
   * @param bnd the binding.
   *
   * @return  a bound value specification.
   */
  public WildcardValueSpecification bound(Binding bnd) {
    final var spec = rcertFactory.createWildcardValueSpecification();
    spec.setBinding(bnd);
    return spec;
  }

  /**
   * Constructs a throwaway binding (generally useful for testing only).
   *
   * @param name the name of the binding.
   *
   * @return the binding.
   */
  public Binding binding(String name) {
    final var bnd = rcertFactory.createBinding();
    bnd.setName(name);
    return bnd;
  }
}
