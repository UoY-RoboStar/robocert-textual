/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.textual.generator.intf.core.SpecGroupParametricField;
import robostar.robocert.textual.generator.tockcsp.core.group.SpecificationGroupElementFinder;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPRefinement;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.TickTockContextGenerator;
import robostar.robocert.SequenceProperty;

/**
 * Generates CSP-M for sequence properties.
 *
 * @author Matt Windsor
 */
public record PropertyGenerator(TickTockContextGenerator tt, CSPStructureGenerator csp,
                                SpecificationGroupElementFinder sf) {

  @Inject
  public PropertyGenerator {
    Objects.requireNonNull(tt);
    Objects.requireNonNull(csp);
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
   * @return the lowered refinement.
   */
  public CSPRefinement lower(SequenceProperty p) {
    return new CSPRefinement(p.isNegated(), p.getInteraction().getGroup(), lhs(p), rhs(p),
        p.getModel());
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
    return switch (it.getType()) {
      case HOLDS -> sequenceRef(it);
      case IS_OBSERVED -> targetRef(it);
    };
  }

  /**
   * Gets the appropriate refinement left-hand side for this sequence property.
   *
   * <p>This is always the mirror image of lhs, with the exception that the rhs of an is-observed
   * property is made to end with timestop.
   *
   * @param it the property for which we are generating CSP.
   * @return the right-hand side process source.
   */
  private CharSequence rhs(SequenceProperty it) {
    // We add timestop to is-observed properties so as to ensure partial traces.
    // TODO(@MattWindsor91): is this compatible with tick-tock reasoning?
    return switch (it.getType()) {
      case HOLDS -> targetRef(it);
      case IS_OBSERVED -> csp.seq(sequenceRef(it), csp.timestop());
    };
  }

  private CharSequence sequenceRef(SequenceProperty it) {
    final var seq = it.getInteraction();
    return csp.namespaced(
        sf.getFullCSPName(seq.getGroup(), SpecGroupParametricField.INTERACTION_MODULE), seq.getName());
  }

  private CharSequence targetRef(SequenceProperty it) {
    return sf.getFullCSPName(it.getInteraction().getGroup(), SpecGroupParametricField.TARGET);
  }
}
