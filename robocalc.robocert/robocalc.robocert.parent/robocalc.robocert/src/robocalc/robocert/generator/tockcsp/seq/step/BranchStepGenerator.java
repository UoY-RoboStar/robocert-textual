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
package robocalc.robocert.generator.tockcsp.seq.step;

import com.google.inject.Inject;
import java.util.stream.Collectors;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.BranchGenerator;
import robocalc.robocert.model.robocert.AlternativeStep;
import robocalc.robocert.model.robocert.BranchStep;
import robocalc.robocert.model.robocert.InterleaveStep;
import robocalc.robocert.model.robocert.Temperature;

/**
 * Generator for branch steps.
 *
 * @author Matt Windsor
 */
public record BranchStepGenerator(CSPStructureGenerator csp,
                                  BranchGenerator bg) {

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
  public BranchStepGenerator {
  }

  /**
   * Generates CSP-M for a branch step.
   *
   * @param b   branch step to generate.
   * @param ctx context of the lifeline for which we are generating CSP-M.
   * @return the generated CSP-M process.
   */
  public CharSequence generate(BranchStep b, LifelineContext ctx) {
    return csp.commented(comment(b), csp.tuple(body(b, ctx)));
  }

  private CharSequence body(BranchStep b, LifelineContext ctx) {
    return b.getBranches().parallelStream().map(x -> csp.tuple(bg.generate(x, ctx)))
        .collect(Collectors.joining(operator(b)));
  }

  /**
   * Gets a debug comment corresponding to the branch step.
   *
   * @param b the step to generate.
   * @return the comment.
   */
  private CharSequence comment(BranchStep b) {
    if (b instanceof InterleaveStep) {
      return "interleave";
    }
    if (b instanceof AlternativeStep a) {
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
  private CharSequence operator(BranchStep b) {
    if (b instanceof InterleaveStep) {
      return INTERLEAVE;
    }
    if (b instanceof AlternativeStep a) {
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