/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.textual.generator.intf.seq.ContextualGenerator;
import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;
import robostar.robocert.textual.generator.intf.seq.fragment.BlockFragmentGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.fragment.until.UntilFragmentHeaderGenerator;
import robostar.robocert.BlockFragment;
import robostar.robocert.DeadlineFragment;
import robostar.robocert.InteractionOperand;
import robostar.robocert.LoopFragment;
import robostar.robocert.OptFragment;
import robostar.robocert.UntilFragment;

/**
 * Implementation of {@link BlockFragmentGenerator}.
 *
 * <p>There is a circular dependency between block fragment generators and themselves through
 * until-fragment generators.
 */
public record BlockFragmentGeneratorImpl
    (CSPStructureGenerator csp, ContextualGenerator<InteractionOperand> operandGen,
     DeadlineFragmentHeaderGenerator deadlineHeaderGen, LoopFragmentHeaderGenerator loopHeaderGen,
     UntilFragmentHeaderGenerator untilHeaderGen) implements BlockFragmentGenerator {

  @Inject
  public BlockFragmentGeneratorImpl {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(operandGen);
    Objects.requireNonNull(deadlineHeaderGen);
    Objects.requireNonNull(loopHeaderGen);
    Objects.requireNonNull(untilHeaderGen);
  }

  @Override
  public CharSequence generate(BlockFragment fragment, LifelineContext ctx) {
    // Special case: if we are generating inside a lifeline attached to one of multiple generated
    // actors, then we delegate UntilFragments to a separate process, and instead emit a
    // synchronisation with that process.
    if (fragment instanceof UntilFragment u) {
      final var i = ctx.untilIndex(u);
      if (0 <= i) {
        return csp.function("UntilSync", ctx.global().untils().channel(), Integer.toString(i));
      }
    }

    return String.join("", generateHeader(fragment, ctx), generateBody(fragment, ctx));
  }

  private CharSequence generateHeader(BlockFragment fragment, LifelineContext ctx) {
    if (fragment instanceof DeadlineFragment d) {
      return deadlineHeaderGen.generate(d, ctx);
    }
    if (fragment instanceof LoopFragment l) {
      return loopHeaderGen.generate(l);
    }
    if (fragment instanceof OptFragment) {
      return "Opt";
    }
    if (fragment instanceof UntilFragment u) {
      return untilHeaderGen.generate(u);
    }

    throw new IllegalArgumentException("unsupported block fragment: %s".formatted(fragment));
  }

  private CharSequence generateBody(BlockFragment fragment, LifelineContext ctx) {
    return csp.tuple(operandGen.generate(fragment.getBody(), ctx));
  }
}
