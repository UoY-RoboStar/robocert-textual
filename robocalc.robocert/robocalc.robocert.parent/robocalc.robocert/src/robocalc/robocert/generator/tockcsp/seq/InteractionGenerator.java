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

import com.google.common.collect.Streams;
import com.google.inject.Inject;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.LetGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.model.robocert.Interaction;

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
      case 0 -> "USTOP";
      case 1 -> csp.seq(generateLifelineBody(s, lines.get(0)), "USTOP");
      default -> csp.seq(generateMultiLifeline(s, lines), "USTOP");
    };
  }

  private CharSequence generateMultiLifeline(Interaction s, List<LifelineContext> lines) {
    // TODO(@MattWindsor91): don't use a StringBuilder here.
    final var sb = new StringBuilder();
    final var nlines = lines.size();
    for (var i = 0; i < nlines; i++) {
      final var line = lines.get(i);
      sb.append(line.procCSP(csp)).append(" ");
      if (i < nlines - 1) {
        sb.append("[| ").append(line.alphaCSP(csp)).append(" |] ");
      }
    }

    return lg.let(alphas(lines), procs(s, lines)).within(sb.toString());
  }

  private CharSequence alphas(List<LifelineContext> lines) {
    // TODO(@MattWindsor91): do NOT synchronise on everything!
    return defs(lines, LifelineContext::alphaCSP, x -> SpecGroupField.UNIVERSE.toString());
  }

  private CharSequence procs(Interaction s, List<LifelineContext> lines) {
    return defs(lines, LifelineContext::procCSP, x -> csp.tuple(generateLifelineBody(s, x)));
  }

  private CharSequence defs(List<LifelineContext> lines,
      BiFunction<LifelineContext, CSPStructureGenerator, CharSequence> lhs,
      Function<LifelineContext, CharSequence> rhs) {
    //noinspection UnstableApiUsage
    return Streams.mapWithIndex(lines.stream(),
        (x, i) -> csp.definition(lhs.apply(x, csp), rhs.apply(x))).collect(Collectors.joining("\n"));
  }

  private CharSequence generateLifelineBody(Interaction s, LifelineContext ctx) {
    return sg.generate(s.getFragments(), ctx);
  }
}
