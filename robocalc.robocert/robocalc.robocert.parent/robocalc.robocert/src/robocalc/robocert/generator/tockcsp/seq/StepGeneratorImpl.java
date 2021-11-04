package robocalc.robocert.generator.tockcsp.seq;

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.StepGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
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
	private UnsupportedSubclassHandler uh;
	@Inject
	private LoadStoreGenerator ls;

	@Override
	public CharSequence generate(SequenceStep it) {
		return generateLoads(it) + generateAfterLoads(it);
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

	private CharSequence generateAfterLoads(SequenceStep it) {
		// Remember to extend this with any non-branch steps added to the
		// metamodel.
		if (it instanceof ActionStep a)
			return ag.generateActionStep(a);
		if (it instanceof BranchStep b)
			return bg.generate(b);
		if (it instanceof DeadlineStep d)
			return dg.generate(d);
		if (it instanceof LoopStep l)
			return lg.generate(l);
		return uh.unsupported(it, "step", "STOP");
	}
}