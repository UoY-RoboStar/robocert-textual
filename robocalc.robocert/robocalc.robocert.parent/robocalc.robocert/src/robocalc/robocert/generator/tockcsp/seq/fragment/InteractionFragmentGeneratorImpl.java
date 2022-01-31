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
import java.util.stream.Stream;
import robocalc.robocert.generator.intf.seq.InteractionFragmentGenerator;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.BlockFragment;
import robocalc.robocert.model.robocert.Branch;
import robocalc.robocert.model.robocert.BranchFragment;
import robocalc.robocert.model.robocert.InteractionFragment;
import robocalc.robocert.model.robocert.LoopFragment;
import robocalc.robocert.model.robocert.OccurrenceFragment;

/**
 * Generator for sequence steps.
 * <p>
 * This generator mainly just delegates into lower-level generators.
 *
 * @author Matt Windsor
 */
public record InteractionFragmentGeneratorImpl(
		OccurrenceFragmentGenerator ag,
		BlockFragmentGenerator blockGen,
		BranchFragmentGenerator branchGen,
		LoadStoreGenerator ls) implements
		InteractionFragmentGenerator {

	@Inject
	public InteractionFragmentGeneratorImpl {
	}

	@Override
	public CharSequence generate(InteractionFragment f, LifelineContext ctx) {
		return generateLoads(f) + generateAfterLoads(f, ctx);
	}

	private String generateLoads(InteractionFragment f) {
		return ls.generateLoads(controlFlowBindings(f)).toString();
	}

	private Stream<Binding> controlFlowBindings(InteractionFragment f) {
		// TODO(@MattWindsor91): make this part of the metamodel?
		if (f instanceof OccurrenceFragment a) {
			return ls.getExprBindings(a);
		}
		if (f instanceof BranchFragment b) {
			return branchBindings(b);
		}
		if (f instanceof LoopFragment l) {
			return ls.getExprBindings(l.getBound());
		}
		return Stream.empty();
	}

	private Stream<Binding> branchBindings(BranchFragment it) {
		return it.getBranches().stream().flatMap(this::branchBindings);
	}

	private Stream<Binding> branchBindings(Branch x) {
		return ls.getExprBindings(x.getGuard());
	}

	private CharSequence generateAfterLoads(InteractionFragment f, LifelineContext ctx) {
		// Remember to extend this with any non-branch steps added to the
		// metamodel.
		if (f instanceof OccurrenceFragment a) {
			return ag.generate(a, ctx);
		}
		if (f instanceof BranchFragment b) {
			return branchGen.generate(b, ctx);
		}
		if (f instanceof BlockFragment b) {
			return blockGen.generate(b, ctx);
		}
		throw new IllegalArgumentException("unsupported fragment type: %s".formatted(f));
	}
}