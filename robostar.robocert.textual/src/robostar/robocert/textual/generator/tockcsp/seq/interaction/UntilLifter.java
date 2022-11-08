/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.seq.interaction;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.textual.generator.intf.seq.context.InteractionContext;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.LetGenerator.LetWithin;
import robostar.robocert.textual.generator.tockcsp.seq.fragment.until.UntilFragmentProcessGenerator;

/**
 * Lifts a multi-lifeline interaction process by attaching a process to handle until fragments.
 *
 * @param csp      CSP structure generator.
 * @param untilGen until fragment process generator.
 * @author Matt Windsor
 */
public record UntilLifter(CSPStructureGenerator csp, UntilFragmentProcessGenerator untilGen) {

  /**
   * Constructs an until lifter.
   *
   * @param csp      CSP structure generator.
   * @param untilGen until fragment process generator.
   */
  @Inject
  public UntilLifter {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(untilGen);
  }

  /**
   * Lifts an interaction process by attaching an until process.
   *
   * @param ctx     interaction context.
   * @param process interaction process as a let-within binding.
   * @return a lifted form of the let-within, now containing an until process if needed.
   */
  public LetWithin lift(InteractionContext ctx, LetWithin process) {
    // Note that the formal RoboCert semantics *always* adds an until process.
    return ctx.untilChannelIfNeeded().map(u -> addUntil(ctx, process, u)).orElse(process);
  }

  private LetWithin addUntil(InteractionContext ctx, LetWithin process, CharSequence untilChan) {
    final var let = process.bindings().and(untilGen.process(ctx));
    final CharSequence body = syncBodyWithUntil(process.body(), untilChan);
    return new LetWithin(let, body);
  }

  private CharSequence syncBodyWithUntil(CharSequence body, CharSequence untilChan) {
    final var lhs = csp.tuple(body);
    final var untilChanSet = csp.sets().enumeratedSet(untilChan);
    final var rhs = UntilFragmentProcessGenerator.NAME;
    return csp.bins().genParallel(lhs, untilChanSet, rhs);
  }
}
