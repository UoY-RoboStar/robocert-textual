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
import robocalc.robocert.model.robocert.Temperature;

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
      return "interleave";
    }
    if (b instanceof AltFragment a) {
      return "alternative (%s)".formatted(a.getTemperature());
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
    if (b instanceof AltFragment a) {
      return altOperator(a.getTemperature());
    }
    throw new IllegalArgumentException("unsupported branch operator: %s".formatted(b));
  }

  /**
   * Expands to the CSP operator for joining together branches on an alternative branch step.
   *
   * @param t the temperature of the alternative step to generate.
   * @return CSP-M external choice if the step is hot; internal otherwise.
   */
  private CharSequence altOperator(Temperature t) {
    return switch (t) {
      case COLD -> INT_CHOICE;
      case HOT -> EXT_CHOICE;
    };
  }
}