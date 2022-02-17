/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.Objects;
import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.core.SpecificationGroupElementFinder;
import robocalc.robocert.generator.tockcsp.core.TargetGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.ll.TickTockContextGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPRefinement;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.SequenceProperty;
import robocalc.robocert.model.robocert.SequencePropertyType;
import robocalc.robocert.model.robocert.Target;

/** Generates CSP-M for sequence properties. */
public record PropertyGenerator(
  TickTockContextGenerator tt,
  CSPStructureGenerator csp,
  RoboCertFactory rf,
  TargetGenerator tg,
  SpecificationGroupElementFinder sf
) {

  @Inject
  public PropertyGenerator {
    Objects.requireNonNull(tt);
    Objects.requireNonNull(csp);
    Objects.requireNonNull(rf);
    Objects.requireNonNull(tg);
    Objects.requireNonNull(sf);
  }

  /**
   * Generates CSP-M for a sequence property.
   *
   * <p>All sequence properties (thus far) are single direction refinements, where the LHS and RHS
   * depend on the sequence property operator.
   *
   * @param p the property to generate.
   * @return the generated property.
   */
  public CharSequence generate(SequenceProperty p) {
    return lower(p).toCSP(tt, csp);
  }

  /**
   * Lowers a sequence property to a CSP refinement.
   *
   * @param p the property to lower.
   *
   * @return the lowered refinement.
   */
  public CSPRefinement lower(SequenceProperty p) {
    return new CSPRefinement(p.isNegated(), lhs(p), rhs(p), getTarget(p), p.getModel());
  }

  private Target getTarget(SequenceProperty p) {
    return p.getInteraction().getGroup().getTarget();
  }

  /**
   * Gets the appropriate refinement left-hand side for this sequence property.
   *
   * <p>This is the target for a 'holds' property, and the interaction for an 'is observed'.
   *
   * @param it the property for which we are generating CSP.
   * @return the left-hand side process source.
   */
  private CharSequence lhs(SequenceProperty it) {
    return sequenceWhenTypeElseTarget(it, SequencePropertyType.HOLDS);
  }

  /**
   * Gets the appropriate refinement left-hand side for this sequence property.
   *
   * <p>This is always the mirror image of lhs.
   *
   * @param it the property for which we are generating CSP.
   * @return the right-hand side process source.
   */
  private CharSequence rhs(SequenceProperty it) {
    return sequenceWhenTypeElseTarget(it, SequencePropertyType.IS_OBSERVED);
  }

  /**
   * @param it the sequence property.
   * @param t the type that it must have for this call to expand to the sequence.
   * @return if the sequence property type of it is t, the sequence of t; else, the instantiated
   *     target of t.
   */
  private CharSequence sequenceWhenTypeElseTarget(SequenceProperty it, SequencePropertyType t) {
    return it.getType() == t ? sequenceRef(it) : targetRef(it);
  }

  private CharSequence sequenceRef(SequenceProperty it) {
    return sf.getFullCSPName(it.getInteraction());
  }

  private CharSequence targetRef(SequenceProperty it) {
    return tg.getFullCSPName(getTarget(it), TargetField.CLOSED);
  }
}
