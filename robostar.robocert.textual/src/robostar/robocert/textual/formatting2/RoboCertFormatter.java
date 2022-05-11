/*
 * generated by Xtext 2.25.0
 */
package robostar.robocert.textual.formatting2;

import circus.robocalc.robochart.textual.formatting2.RoboChartFormatter;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import robostar.robocert.CertPackage;
import robostar.robocert.Group;
import robostar.robocert.SpecificationGroup;

@SuppressWarnings("unused")
public class RoboCertFormatter extends RoboChartFormatter {
  // TODO(@MattWindsor91): this whole thing needs implementing

  protected void format(CertPackage certPackage, IFormattableDocument doc) {
    // TODO: format HiddenRegions around keywords, attributes, cross references, etc.
    for (Group group : certPackage.getGroups()) {
      doc.format(group);
    }
  }

  protected void format(SpecificationGroup group, IFormattableDocument doc) {
    // TODO: format HiddenRegions around keywords, attributes, cross references, etc.
    for (var seq : group.getInteractions()) {
      doc.format(seq);
    }
  }

  // TODO: implement for Instantiation, ConstAssignment, AssertionGroup, Assertion, LogicalExpr,
  // RelationExpr, MinusExpr, CSPGroup, SpecificationGroup, Sequence, Subsequence, ActionStep,
  // DeadlineStep, LoopFragment, BlockFragment, DefiniteLoopBound, LowerLoopBound, RangeLoopBound,
  // BranchFragment, Branch, ExprGuard, MessageOccurrence, WaitAction, Message, NamedMessageSet,
  // BinaryMessageSet, ExtensionalMessageSet, ExpressionArgument, WildcardArgument
}