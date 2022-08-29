/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.core.group;

import com.google.inject.Inject;
import robostar.robocert.textual.generator.intf.core.SpecGroupField;
import robostar.robocert.textual.generator.intf.core.SpecGroupParametricField;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.utils.name.GroupNamer;
import robostar.robocert.SpecificationGroup;

/**
 * Abstracts over the act of getting CSP names for specification group components.
 *
 * <p>This mainly exists to break a dependency cycle between the CSP generators and sequence
 * generators.
 */
public class SpecificationGroupElementFinder {
  @Inject private CSPStructureGenerator csp;
  @Inject private GroupNamer gn;

  /**
   * Gets the fully qualified CSP name of a field on a sequence group's parametric part.
   *
   * <p>We assume that we want the closed form of the parametric part.
   *
   * @param it the group whose field is to be located.
   * @param field the field in question.
   * @return CSP-M expanding to a module-qualified name of a field.
   */
  public CharSequence getFullCSPName(SpecificationGroup it, SpecGroupParametricField field) {
    return csp.namespaced(getFullCSPName(it, SpecGroupField.PARAMETRIC_CLOSED), field.toString());
  }

  /**
   * Gets the fully qualified CSP name of a field on a sequence group.
   *
   * @param it the group whose field is to be located.
   * @param field the field in question.
   * @return CSP-M expanding to a module-qualified name of a field.
   */
  public CharSequence getFullCSPName(SpecificationGroup it, SpecGroupField field) {
    return csp.namespaced(gn.getOrSynthesiseName(it), field.toString());
  }
}
