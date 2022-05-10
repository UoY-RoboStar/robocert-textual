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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.generator.intf.seq.ContextualGenerator;
import robocalc.robocert.generator.intf.seq.context.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.model.robocert.BranchFragment;
import robocalc.robocert.model.robocert.ElseGuard;
import robocalc.robocert.model.robocert.EmptyGuard;
import robocalc.robocert.model.robocert.ExprGuard;
import robocalc.robocert.model.robocert.Guard;
import robocalc.robocert.model.robocert.InteractionOperand;
import robocalc.robocert.model.robocert.util.StreamHelper;

/**
 * Generates CSP-M for interaction operands and guards.
 *
 * @author Matt Windsor
 */
public record InteractionOperandGenerator(ExpressionGenerator exprGen,
                                          SubsequenceGenerator subseqGen) implements
    ContextualGenerator<InteractionOperand> {

  /**
   * Constructs an interaction operand generator.
   *
   * @param exprGen   an expression generator.
   * @param subseqGen a subsequence generator.
   */
  @Inject
  public InteractionOperandGenerator {
    Objects.requireNonNull(exprGen);
    Objects.requireNonNull(subseqGen);
  }

  @Override
  public CharSequence generate(InteractionOperand b, LifelineContext ctx) {
    // No whitespace because the empty guard should be a no-op on the body.
    return String.join("", guard(b.getGuard()), subseqGen.generate(b.getFragments(), ctx));
  }

  private CharSequence guard(Guard g) {
    if (g instanceof EmptyGuard) {
      return "";
    }
    if (g instanceof ExprGuard e) {
      return "%s & ".formatted(exprGen.generate(e.getExpr()));
    }
    if (g instanceof ElseGuard l) {
      return "{- else -} not %s & ".formatted(elseGuard(l));
    }
    throw new IllegalArgumentException("unsupported guard type: %s".formatted(g));
  }

  private CharSequence elseGuard(ElseGuard l) {
    return neighbourExprGuards(l).map(ExprGuard::getExpr).map(exprGen::generate)
        .collect(Collectors.joining(" and ", "(", ")"));
  }

  private Stream<ExprGuard> neighbourExprGuards(ElseGuard l) {
    return StreamHelper.filter(neighbourGuards(l), ExprGuard.class);
  }

  private Stream<Guard> neighbourGuards(ElseGuard l) {
    final var branchFrag = EcoreUtil2.getContainerOfType(l, BranchFragment.class);
    // NOTE(@MattWindsor91): this doesn't filter out ElseGuards, check whether this is a problem?
    return Optional.ofNullable(branchFrag).stream()
        .flatMap(f -> f.getBranches().stream().map(InteractionOperand::getGuard));
  }
}
