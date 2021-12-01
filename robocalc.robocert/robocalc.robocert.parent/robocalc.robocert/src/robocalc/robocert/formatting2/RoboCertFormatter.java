/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.formatting2;

import circus.robocalc.robochart.textual.formatting2.RoboChartFormatter;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import robocalc.robocert.model.robocert.CertPackage;
import robocalc.robocert.model.robocert.Group;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetGroup;

public class RoboCertFormatter extends RoboChartFormatter {

	protected void format(CertPackage certPackage, IFormattableDocument doc) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (Group group : certPackage.getGroups()) {
			doc.format(group);
		}
	}

	protected void format(TargetGroup targetGroup, IFormattableDocument doc) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (Target target : targetGroup.getTargets()) {
			doc.format(target);
		}
	}
	
	// TODO: implement for Instantiation, ConstAssignment, AssertionGroup, Assertion, LogicalExpr, RelationExpr, MinusExpr, CSPGroup, SequenceGroup, Sequence, Subsequence, ActionStep, DeadlineStep, LoopStep, BlockStep, DefiniteLoopBound, LowerLoopBound, RangeLoopBound, BranchStep, Branch, ExprGuard, ArrowAction, WaitAction, MessageSpec, NamedMessageSet, BinaryMessageSet, ExtensionalMessageSet, ExpressionArgument, WildcardArgument
}