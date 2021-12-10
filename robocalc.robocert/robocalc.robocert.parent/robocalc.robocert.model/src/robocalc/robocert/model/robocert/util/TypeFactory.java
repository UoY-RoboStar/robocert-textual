/*******************************************************************************
 * Copyright (c) 2021 University of York and others
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

import circus.robocalc.robochart.PrimitiveType;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.TypeDecl;
import circus.robocalc.robochart.TypeRef;
import com.google.inject.Inject;

/**
 * High-level shortcuts for constructing RoboChart type references.
 *
 * <p>These are mainly useful for <i>testing</i> the RoboCert model, but included here for
 * completeness.
 *
 * @author Matt Windsor
 */
public record TypeFactory(RoboChartFactory chartFactory) {
  // TODO(@MattWindsor91): is this duplicating anything in the RoboChart generator?

  /**
   * Constructs a type factory.
   *
   * @param chartFactory the underlying RoboChart factory.
   */
  @Inject
  public TypeFactory {}

  /**
   * Shorthand for building a type reference.
   *
   * @param d the type declaration to refer-to.
   * @return the type reference.
   */
  public TypeRef ref(TypeDecl d) {
    final var t = chartFactory.createTypeRef();
    t.setRef(d);
    return t;
  }

  /**
   * Shorthand for building a primitive type.
   *
   * <p>One should not use this to refer to existing standard RoboChart primitive types; instead,
   * search the resource set for those types.  This mainly exists for testing.
   *
   * @param name the name of the primitive type.
   * @return the primitive type.
   */
  public PrimitiveType prim(String name) {
    final var p = chartFactory.createPrimitiveType();
    p.setName(name);
    return p;
  }

  /**
   * Shorthand for building a reference to a primitive type.
   *
   * <p>Per the documentation of {@code prim}, this mainly exists for testing.
   *
   * @param name the name of the primitive type.
   * @return the primitive type reference.
   */
  public TypeRef primRef(String name) {
    return ref(prim(name));
  }
}
