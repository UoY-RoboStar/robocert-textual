package robocalc.robocert.generator.tockcsp.seq;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;
import robocalc.robocert.model.robocert.SequenceStep;
import robocalc.robocert.model.robocert.ActionStep;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.Branch;
import robocalc.robocert.model.robocert.LoopStep;
import robocalc.robocert.model.robocert.DeadlineStep;
import robocalc.robocert.generator.intf.seq.StepGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
import robocalc.robocert.model.robocert.BranchStep;

/**
 * Generator for sequence steps.
 * 
 * This generator mainly just delegates into lower-level generators.
 * 
 * @author Matt Windsor
 */
public class StepGeneratorImpl implements StepGenerator {
	@Inject private ActionStepGenerator ag;
	@Inject private BranchStepGenerator bg;
	@Inject private DeadlineStepGenerator dg;
	@Inject private LoopStepGenerator lg;
	@Inject private UnsupportedSubclassHandler uh;
	@Inject private LoadStoreGenerator ls;
	
	@Override
	public CharSequence generate(SequenceStep it) {
		return generateLoads(it) + generateAfterLoads(it);
	}
	
	private String generateLoads(SequenceStep it) {
		return ls.generateLoads(controlFlowBindings(it).toArray(new Binding[0])).toString();
	}
	
	private List<Binding> controlFlowBindings(SequenceStep it) {
		// TODO(@MattWindsor91): make this part of the metamodel?
		if (it instanceof ActionStep a)
			return ls.getExprBindings(a);
		if (it instanceof BranchStep b)
			return branchBindings(b);
		if (it instanceof LoopStep l)
			return ls.getExprBindings(l.getBound());
		return List.of();
	}
	
	private List<Binding> branchBindings(BranchStep it) {
		return it.getBranches().stream().flatMap((x) -> branchBindings(x)).collect(Collectors.toList());
	}

	private Stream<Binding> branchBindings(Branch x) {
		return ls.getExprBindings(x.getGuard()).stream();
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