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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.generator.utils.MemoryFactory;
import robocalc.robocert.model.robocert.Sequence;

/**
 * Generates sequences.
 *
 * @author Matt Windsor
 */
public class SequenceGenerator {
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private MemoryFactory mf;
	@Inject
	private SubsequenceGenerator sg;
	@Inject
	private ModuleGenerator mg;
	@Inject
	private LifelineContextFactory lcf;

	/**
	 * Generates CSP-M for a sequence.
	 *
	 * If the sequence needs a memory, it will be lifted into the appropriate
	 * context.
	 *
	 * @param s the sequence for which we are generating CSP-M.
	 *
	 * @return the generated CSP-M for the sequence.
	 */
	public CharSequence generate(Sequence s) {
		// TODO(@MattWindsor91): work out whether the memory is shared between
		// lifelines, or unique to each.
		var inner = generateWithoutMemory(s);
		return mf.hasMemory(s) ? mg.lift(s, inner) : csp.tuple(inner);
	}

	private CharSequence generateWithoutMemory(Sequence it) {
		var lines = lcf.createContexts(it);
		return switch (lines.size()) {
		case 0 -> "{- no lifelines? -} STOP";
		case 1 -> generateLifelineBody(it, lines.get(0));
		default -> generateMulti(it, lines);
		};
	}

	private CharSequence generateMulti(Sequence s, List<LifelineContext> lines) {
		return csp.let(alphas(s, lines), procs(s, lines)).within(lineComposition(lines));
	}

	private CharSequence alphas(Sequence s, List<LifelineContext> lines) {
		// TODO(@MattWindsor91): do NOT synchronise on everything!
		return defs("alpha", lines, x -> MessageSetGenerator.QUALIFIED_UNIVERSE_NAME);
	}

	private CharSequence procs(Sequence s, List<LifelineContext> lines) {
		return defs("proc", lines, x -> generateLifelineBody(s, x));
	}

	private CharSequence defs(String type, List<LifelineContext> lines, Function<LifelineContext, CharSequence> f) {
		return Streams
				.mapWithIndex(lines.stream(),
						(x, i) -> csp.definition(csp.function(type, Long.toString(i)), f.apply(x)))
				.collect(Collectors.joining("\n"));
	}

	private String lineComposition(List<LifelineContext> lines) {
		var lastLine = lines.size() - 1;
		// this should line up with alphas() and procs() above
		return "|| line : {0..%d} @ [alpha(line)] proc(line)".formatted(lastLine);
	}

	private CharSequence generateLifelineBody(Sequence s, LifelineContext line) {
		// TODO(@MattWindsor91): push through line

		// TODO(@MattWindsor91): elide TCHAOS if not necessary
		var chaos = csp.function("TCHAOS", MessageSetGenerator.QUALIFIED_UNIVERSE_NAME);
		return String.join("\n", sg.generate(s.getBody()), "; -- end of defined steps", chaos);
	}
}