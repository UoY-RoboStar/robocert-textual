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
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.xtext.EcoreUtil2;
import robostar.robocert.textual.generator.intf.core.SpecGroupField;
import robostar.robocert.textual.generator.intf.seq.context.ActorContext;
import robostar.robocert.textual.generator.intf.seq.context.InteractionContext;
import robostar.robocert.textual.generator.intf.seq.SubsequenceGenerator;
import robostar.robocert.textual.generator.tockcsp.core.tgt.TerminationGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.LetGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.SetGenerator;
import robostar.robocert.textual.generator.tockcsp.memory.ModuleGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.interaction.UntilLifter;
import robostar.robocert.textual.generator.tockcsp.seq.message.MessageGenerator;
import robostar.robocert.Interaction;
import robostar.robocert.Message;
import robostar.robocert.util.StreamHelper;

/**
 * Generates the top-level CSP-M infrastructure for {@link Interaction}s.
 *
 * @author Matt Windsor
 */
public class InteractionGenerator {

  @Inject
  private LetGenerator lg;
  @Inject
  private CSPStructureGenerator csp;
  @Inject
  private SubsequenceGenerator sg;
  @Inject
  private ModuleGenerator mg;
  @Inject
  private ActorGenerator actorGen;
  @Inject
  private SyncChannelGenerator syncGen;
  @Inject
  private TerminationGenerator termGen;
  @Inject
  private MessageGenerator msgGen;
  @Inject
  private UntilLifter untilLifter;

  /**
   * Generates CSP-M for a sequence.
   *
   * <p>If the sequence needs a memory, it will be lifted into the appropriate context.
   *
   * @param s the context-lifted sequence for which we are generating CSP-M.
   * @return the generated CSP-M for the sequence.
   */
  public CharSequence generate(InteractionContext s) {
    // TODO(@MattWindsor91): work out whether the memory is shared between
    // lifelines, or unique to each.
    final var inner = generateWithoutMemory(s);
    return mg.needsMemory(s.seq().getVariables()) ? mg.lift(s.seq(), inner) : csp.tuple(inner);
  }

  private CharSequence generateWithoutMemory(InteractionContext s) {
    final var lines = s.actors(actorGen);

    // Optimise single-lifeline processes by directly generating the body without trying to produce
    // alphabets, until processes, etc.
    //
    // The default case is the one that corresponds to the semantics in the manual.
    return switch (lines.size()) {
      case 0 -> csp.skip();
      case 1 -> sg.generate(s.seq().getFragments(), lines.get(0));
      default -> generateMultiLifeline(s, lines);
    };
  }


  private CharSequence generateMultiLifeline(InteractionContext s, List<ActorContext> lines) {
    final var cs = csp.sets();

    final var ctrl = syncGen.qualified(syncGen.ctrlSetName(s.seq()));

    final var alphas = defs(lines, ActorContext::alphaCSP, x -> alpha(x, ctrl));

    final CharSequence procs = multiLifelineProcesses(s, lines, cs);

    final var let = lg.let(alphas, procs).within(mainProcess());
    final var body = untilLifter.lift(s, let);

    // If we're using an until sync channel, we need to hide it, as it isn't part of the
    // RoboChart process semantics.  Same for the termination channel (as we don't use the
    // RoboChart ones).
    return csp.bins().hide(cs.tuple(body.toString()), ctrl);
  }

  private CharSequence multiLifelineProcesses(InteractionContext s, List<ActorContext> lines,
      SetGenerator cs) {
    return defs(lines, ActorContext::procCSP,
        x -> cs.tuple(csp.seq(sg.generate(s.seq().getFragments(), x), termGen.terminateProc())));
  }

  private CharSequence mainProcess() {
    return csp.iterAlphaParallel(SpecGroupField.ACTOR_ENUM.toString(), ActorContext.ALPHA_FUNCTION,
        ActorContext.PROC_FUNCTION);
  }



  private CharSequence defs(List<ActorContext> lines,
      BiFunction<ActorContext, CSPStructureGenerator, CharSequence> lhs,
      Function<ActorContext, CharSequence> rhs) {
    return lines.stream().map(x -> csp.definition(lhs.apply(x, csp), rhs.apply(x)))
        .collect(Collectors.joining("\n"));
  }

  private CharSequence alpha(ActorContext ctx, CharSequence ctrl) {
    // If we're using alphabet sets, we'll be using a separate process to handle UntilFragments.
    // This means that the set of events handled directly by a lifeline is precisely that defined
    // by its messages.
    final var messages = EcoreUtil2.eAllOfType(ctx.global().seq(), Message.class);
    final var msgSets = messages.stream()
        .filter(m -> ctx.isForAnyOf(Stream.of(m.getFrom(), m.getTo())))
        .map(msgGen::generateCSPEventSet);

    // This bit is convoluted, but intended to reduce duplicates.
    final var sets = StreamHelper.push(ctrl, msgSets).map(CharSequence::toString)
        .collect(Collectors.toUnmodifiableSet());
    return csp.iteratedUnion(csp.set(sets.toArray(CharSequence[]::new)));
  }
}
