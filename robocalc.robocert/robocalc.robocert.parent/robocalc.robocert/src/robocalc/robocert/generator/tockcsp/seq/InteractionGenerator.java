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
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.LetGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageGenerator;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.Message;

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
  private LifelineContextFactory lcf;
  @Inject
  private MessageGenerator msgGen;

  /**
   * Generates CSP-M for a sequence.
   *
   * <p>If the sequence needs a memory, it will be lifted into the appropriate context.
   *
   * @param s the sequence for which we are generating CSP-M.
   * @return the generated CSP-M for the sequence.
   */
  public CharSequence generate(Interaction s) {
    // TODO(@MattWindsor91): work out whether the memory is shared between
    // lifelines, or unique to each.
    final var inner = generateWithoutMemory(s);
    return elideMemory(s) ? csp.tuple(inner) : mg.lift(s, inner);
  }

  /**
   * Can we safely get away with not emitting a memory?
   *
   * @param s the interaction for which we are generating CSP-M.
   * @return true if, and only if, there is no need to emit a memory for this interaction.
   */
  private boolean elideMemory(Interaction s) {
    final var variables = s.getVariables();
    return variables == null || variables.getVars().isEmpty();
  }

  private CharSequence generateWithoutMemory(Interaction s) {
    final var lines = lcf.createContexts(s);
    return switch (lines.size()) {
      case 0 -> csp.timestop();
      case 1 -> csp.seq(sg.generate(s.getFragments(), lines.get(0)), csp.timestop());
      default -> generateMultiLifeline(s, lines);
    };
  }

  private CharSequence generateMultiLifeline(Interaction s, List<LifelineContext> lines) {
    final var alphas = defs(lines, LifelineContext::alphaCSP,
        x -> alpha(s, x));

    final var procs = defs(lines, LifelineContext::procCSP,
        x -> csp.tuple(sg.generate(s.getFragments(), x)));

    final var body = csp.iterAlphaParallel(SpecGroupParametricField.ACTOR_ENUM.toString(),
        LifelineContext.ALPHA_FUNCTION, LifelineContext.PROC_FUNCTION);

    return lg.let(alphas, procs).within(csp.seq(body, csp.timestop()));
  }

  private CharSequence defs(List<LifelineContext> lines,
      BiFunction<LifelineContext, CSPStructureGenerator, CharSequence> lhs,
      Function<LifelineContext, CharSequence> rhs) {
    return lines.stream().map(x -> csp.definition(lhs.apply(x, csp), rhs.apply(x)))
        .collect(Collectors.joining("\n"));
  }

  private CharSequence alpha(Interaction s, LifelineContext ctx) {
    // If we're using alphabet sets, we'll be using a separate process to handle UntilFragments.
    // This means that the set of events handled directly by a lifeline is precisely that defined
    // by its messages.
    final var messages = EcoreUtil2.eAllOfType(s, Message.class);
    final var sets = messages.stream()
        .filter(m -> ctx.isForAnyOf(Stream.of(m.getFrom(), m.getTo())))
        .map(msgGen::generateCSPEventSet).toArray(CharSequence[]::new);
    return csp.iteratedUnion(csp.set(sets));
  }
}
