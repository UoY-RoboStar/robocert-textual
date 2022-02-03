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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.LetGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
import robocalc.robocert.generator.utils.MemoryFactory;
import robocalc.robocert.model.robocert.Sequence;

/**
 * Generates the top-level infrastructure for sequences.
 *
 * @author Matt Windsor
 */
public class SequenceGenerator {
	@Inject
	private LetGenerator lg;
	@Inject
	private MessageSetGenerator msg;
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

	private CharSequence generateWithoutMemory(Sequence s) {
		var lines = lcf.createContexts(s);
		// Technically, we don't really need a let-within here if we only have
		// one process, but it simplifies some of the rest of the generator to
		// not special-case that.
		var body = csp.iterAlphaParallel(lines.size(), LifelineContext.ALPHA_FUNCTION, LifelineContext.PROC_FUNCTION);
		return lg.let(alphas(s, lines), procs(s, lines)).within(body);
	}

	private CharSequence alphas(Sequence s, List<LifelineContext> lines) {
		// TODO(@MattWindsor91): do NOT synchronise on everything!
		return defs(lines, LifelineContext::alphaCSP, x -> msg.qualifiedUniverseName());
	}

	private CharSequence procs(Sequence s, List<LifelineContext> lines) {
		return defs(lines, LifelineContext::procCSP, x -> csp.tuple(generateLifelineBody(s, x)));
	}

	private CharSequence defs(List<LifelineContext> lines, BiFunction<LifelineContext, CSPStructureGenerator, CharSequence> lhs, Function<LifelineContext, CharSequence> rhs) {
		return Streams
				.mapWithIndex(lines.stream(),
						(x, i) -> csp.definition(lhs.apply(x, csp), rhs.apply(x)))
				.collect(Collectors.joining());
	}

	private CharSequence generateLifelineBody(Sequence s, LifelineContext ctx) {
		return sg.generate(s.getBody(), ctx);
	}
}