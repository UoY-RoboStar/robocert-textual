package robocalc.robocert.scoping

import robocalc.robocert.model.robocert.OperationTopic
import org.eclipse.xtext.scoping.IScope
import robocalc.robocert.generator.utils.TargetExtensions
import com.google.inject.Inject
import robocalc.robocert.utils.MessageAnalysis
import robocalc.robocert.utils.ArrowDirection
import robocalc.robocert.generator.utils.ContextExtensions
import org.eclipse.xtext.scoping.Scopes

/**
 * Scoping logic for message topics.
 */
class TopicScopeExtensions {
	@Inject extension ContextExtensions
	@Inject extension TargetExtensions

	/**
	 * Calculates the scope of operations available to the given topic.
	 * 
	 * @param it  the topic for which we are getting scoping information.
	 * 
	 * @return the scope (may be null).
	 */
	def IScope getOperationScope(OperationTopic it) {
		Scopes.scopeFor(MessageAnalysis.analyse(spec).visibleContexts.flatMap[allOperations].toSet)
	}
	
	/**
	 * Gets the contexts that an analysed message should be able to reach.
	 */
	def getVisibleContexts(MessageAnalysis it) {
		switch direction {
			case ArrowDirection::Output:
				// A message going away from the target can reach anything in
				// the target's corresponding world.
				target.world
			case Input: null // TODO(@MattWindsor91): input
			case Unknown: null
		}
	}
}