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
import robocalc.robocert.generator.intf.seq.StepGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.model.robocert.ActionStep;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.Branch;
import robocalc.robocert.model.robocert.BranchStep;
import robocalc.robocert.model.robocert.DeadlineStep;
import robocalc.robocert.model.robocert.LoopStep;
import robocalc.robocert.model.robocert.SequenceStep;

/**
 * Generator for sequence steps.
 *
 * This generator mainly just delegates into lower-level generators.
 *
 * @author Matt Windsor
 */
public class StepGeneratorImpl implements StepGenerator {
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
	public CharSequence generate(SequenceStep s, LifelineContext ctx) {
		return generateLoads(s) + generateAfterLoads(s, ctx);
	}

	private String generateLoads(SequenceStep it) {
		return ls.generateLoads(controlFlowBindings(it)).toString();
	}

	private Stream<Binding> controlFlowBindings(SequenceStep it) {
		// TODO(@MattWindsor91): make this part of the metamodel?
		if (it instanceof ActionStep a)
			return ls.getExprBindings(a);
		if (it instanceof BranchStep b)
			return branchBindings(b);
		if (it instanceof LoopStep l)
			return ls.getExprBindings(l.getBound());
		return Stream.empty();
	}

	private Stream<Binding> branchBindings(BranchStep it) {
		return it.getBranches().stream().flatMap(this::branchBindings);
	}

	private Stream<Binding> branchBindings(Branch x) {
		return ls.getExprBindings(x.getGuard());
	}

	private CharSequence generateAfterLoads(SequenceStep s, LifelineContext ctx) {
		// Remember to extend this with any non-branch steps added to the
		// metamodel.
		if (s instanceof ActionStep a)
			return ag.generate(a, ctx);
		if (s instanceof BranchStep b)
			return bg.generate(b, ctx);
		if (s instanceof DeadlineStep d)
			return dg.generate(d, ctx);
		if (s instanceof LoopStep l)
			return lg.generate(l, ctx);
		throw new IllegalArgumentException("unsupported step type: %s".formatted(s));
	}
}