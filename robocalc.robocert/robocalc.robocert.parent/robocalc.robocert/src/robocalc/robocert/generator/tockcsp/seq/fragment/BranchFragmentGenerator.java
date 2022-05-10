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
package robocalc.robocert.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Collector;

import robocalc.robocert.generator.intf.seq.ContextualGenerator;
import robocalc.robocert.generator.intf.seq.context.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.AltFragment;
import robocalc.robocert.model.robocert.BranchFragment;
import robocalc.robocert.model.robocert.InteractionOperand;
import robocalc.robocert.model.robocert.ParFragment;
import robocalc.robocert.model.robocert.XAltFragment;

/**
 * Generator for branch fragments.
 *
 * @author Matt Windsor
 */
public record BranchFragmentGenerator(CSPStructureGenerator csp,
                                      ContextualGenerator<InteractionOperand> bg) {

  @Inject
  public BranchFragmentGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(bg);
  }

  /**
   * Generates CSP-M for a branch step.
   *
   * @param b   branch step to generate.
   * @param ctx context of the lifeline for which we are generating CSP-M.
   * @return the generated CSP-M process.
   */
  public CharSequence generate(BranchFragment b, LifelineContext ctx) {
    final var body = b.getBranches().parallelStream().map(x -> csp.tuple(bg.generate(x, ctx)))
        .collect(operator(b));

    return csp.commented(comment(b), csp.tuple(body));
  }

  /**
   * Gets a debug comment corresponding to the branch step.
   *
   * @param b the step to generate.
   * @return the comment.
   */
  private CharSequence comment(BranchFragment b) {
    if (b instanceof ParFragment) {
      return "par";
    }
    if (b instanceof AltFragment) {
      return "alt";
    }
    if (b instanceof XAltFragment) {
      return "xalt";
    }
    // This will result in an exception later anyway.
    return "?";
  }

  /**
   * Gets the CSP-M operator corresponding to the branch step.
   *
   * @param b the step to generate.
   * @return the corresponding operator as a collector over branch bodies.
   */
  private Collector<CharSequence, ?, String> operator(BranchFragment b) {
    final var cb = csp.bins();
    if (b instanceof ParFragment) {
      return cb.toInterleave();
    }
    if (b instanceof AltFragment) {
      return cb.toIntChoice();
    }
    if (b instanceof XAltFragment) {
      return cb.toExtChoice();
    }
    throw new IllegalArgumentException("unsupported branch operator: %s".formatted(b));
  }
}