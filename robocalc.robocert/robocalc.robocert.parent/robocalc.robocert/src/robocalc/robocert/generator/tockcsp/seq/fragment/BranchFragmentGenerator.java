/********************************************************************************
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
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import java.util.stream.Collectors;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.InteractionOperandGenerator;
import robocalc.robocert.model.robocert.AltFragment;
import robocalc.robocert.model.robocert.BranchFragment;
import robocalc.robocert.model.robocert.ParFragment;
import robocalc.robocert.model.robocert.XAltFragment;

/**
 * Generator for branch fragments.
 *
 * @author Matt Windsor
 */
public record BranchFragmentGenerator(CSPStructureGenerator csp,
                                      InteractionOperandGenerator bg) {

  /**
   * The CSP-M external choice operator.
   */
  private static final String EXT_CHOICE = "[]";
  /**
   * The CSP-M internal choice operator.
   */
  private static final String INT_CHOICE = "|~|";
  /**
   * The CSP-M interleave operator.
   */
  private static final String INTERLEAVE = "|||";

  @Inject
  public BranchFragmentGenerator {
  }

  /**
   * Generates CSP-M for a branch step.
   *
   * @param b   branch step to generate.
   * @param ctx context of the lifeline for which we are generating CSP-M.
   * @return the generated CSP-M process.
   */
  public CharSequence generate(BranchFragment b, LifelineContext ctx) {
    return csp.commented(comment(b), csp.tuple(body(b, ctx)));
  }

  private CharSequence body(BranchFragment b, LifelineContext ctx) {
    return b.getBranches().parallelStream().map(x -> csp.tuple(bg.generate(x, ctx)))
        .collect(Collectors.joining(operator(b)));
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
   * @return the corresponding CSP-M.
   */
  private CharSequence operator(BranchFragment b) {
    if (b instanceof ParFragment) {
      return INTERLEAVE;
    }
    if (b instanceof AltFragment) {
      return INT_CHOICE;
    }
    if (b instanceof XAltFragment) {
      return EXT_CHOICE;
    }
    throw new IllegalArgumentException("unsupported branch operator: %s".formatted(b));
  }
}