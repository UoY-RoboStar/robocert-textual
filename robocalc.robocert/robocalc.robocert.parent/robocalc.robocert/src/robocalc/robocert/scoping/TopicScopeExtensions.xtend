package robocalc.robocert.scoping

import robocalc.robocert.model.robocert.OperationTopic
import org.eclipse.xtext.scoping.IScope
import robocalc.robocert.generator.utils.TargetExtensions
import com.google.inject.Inject
import robocalc.robocert.generator.utils.ContextExtensions
import org.eclipse.xtext.scoping.Scopes
import circus.robocalc.robochart.Context
import java.util.Iterator
import org.eclipse.emf.ecore.EObject
import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.MessageTopic

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
	def IScope getEventScope(EventTopic it) {
		it.scope[allEvents.map[it as EObject]]
	}

	/**
	 * Calculates the scope of operations available to the given topic.
	 * 
	 * @param it  the topic for which we are getting scoping information.
	 * 
	 * @return the scope (may be null).
	 */
	def IScope getOperationScope(OperationTopic it) {
		it.scope[allOperations.map[it as EObject]]
	}
	
	private def scope(MessageTopic it, (Context) => Iterator<EObject> selector) {
		// TODO(@MattWindsor91): need to work out what to do with inbound operations;
		// can they ever happen?
		val set = spec?.target?.world?.flatMap(selector)?.toSet;
		if (set !== null) {
			Scopes.scopeFor(set)
		}
	}
}