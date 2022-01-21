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

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.InteractionFragmentGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.Branch;
import robocalc.robocert.model.robocert.BranchStep;
import robocalc.robocert.model.robocert.DeadlineStep;
import robocalc.robocert.model.robocert.LoopStep;
import robocalc.robocert.model.robocert.OccurrenceFragment;
import robocalc.robocert.model.robocert.InteractionFragment;

/**
 * Generator for sequence steps.
 *
 * This generator mainly just delegates into lower-level generators.
 *
 * @author Matt Windsor
 */
public class InteractionFragmentGeneratorImpl implements InteractionFragmentGenerator {
	@Inject
	private ActionStepGenerator ag;
	@Inject
	private BranchStepGenerator bg;
	@Inject
	private DeadlineStepGenerator dg;
	@Inject
	private LoopStepGenerator lg;
	@Inject
	private LoadStoreGenerator ls;

	@Override
	public CharSequence generate(InteractionFragment f, LifelineContext ctx) {
		return generateLoads(f) + generateAfterLoads(f, ctx);
	}

	private String generateLoads(InteractionFragment f) {
		return ls.generateLoads(controlFlowBindings(f)).toString();
	}

	private Stream<Binding> controlFlowBindings(InteractionFragment f) {
		// TODO(@MattWindsor91): make this part of the metamodel?
		if (f instanceof OccurrenceFragment a)
			return ls.getExprBindings(a);
		if (f instanceof BranchStep b)
			return branchBindings(b);
		if (f instanceof LoopStep l)
			return ls.getExprBindings(l.getBound());
		return Stream.empty();
	}

	private Stream<Binding> branchBindings(BranchStep it) {
		return it.getBranches().stream().flatMap(this::branchBindings);
	}

	private Stream<Binding> branchBindings(Branch x) {
		return ls.getExprBindings(x.getGuard());
	}

	private CharSequence generateAfterLoads(InteractionFragment f, LifelineContext ctx) {
		// Remember to extend this with any non-branch steps added to the
		// metamodel.
		if (f instanceof OccurrenceFragment a)
			return ag.generate(a, ctx);
		if (f instanceof BranchStep b)
			return bg.generate(b, ctx);
		if (f instanceof DeadlineStep d)
			return dg.generate(d, ctx);
		if (f instanceof LoopStep l)
			return lg.generate(l, ctx);
		throw new IllegalArgumentException("unsupported step type: %s".formatted(f));
	}
}