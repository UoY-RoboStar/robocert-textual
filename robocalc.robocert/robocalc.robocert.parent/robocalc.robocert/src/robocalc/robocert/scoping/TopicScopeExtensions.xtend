package robocalc.robocert.scoping

import robocalc.robocert.model.robocert.OperationTopic
import org.eclipse.xtext.scoping.IScope
import robocalc.robocert.generator.utils.TargetExtensions
import com.google.inject.Inject
import org.eclipse.xtext.scoping.Scopes
import circus.robocalc.robochart.Context
import org.eclipse.emf.ecore.EObject
import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.MessageTopic
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils
import robocalc.robocert.generator.utils.EObjectExtensions
import java.util.stream.Stream
import java.util.stream.Collectors

/**
 * Scoping logic for message topics.
 */
class TopicScopeExtensions {
	@Inject extension CTimedGeneratorUtils
	@Inject extension TargetExtensions
	@Inject extension EObjectExtensions

	/**
	 * Calculates the scope of operations available to the given topic.
	 * 
	 * @param it  the topic for which we are getting scoping information.
	 * 
	 * @return the scope (may be null).
	 */
	def IScope getEventScope(EventTopic it) {
		it.scope[allEvents.map[it as EObject].stream]
	}

	/**
	 * Calculates the scope of operations available to the given topic.
	 * 
	 * @param it  the topic for which we are getting scoping information.
	 * 
	 * @return the scope (may be null).
	 */
	def IScope getOperationScope(OperationTopic it) {
		it.scope[allOperations.map[it as EObject].stream]
	}
	
	private def scope(MessageTopic it, (Context) => Stream<EObject> selector) {
		// TODO(@MattWindsor91): need to work out what to do with inbound operations;
		// can they ever happen?
		val set = spec?.targetOfParentGroup?.contexts?.flatMap(selector)?.collect(Collectors::toSet);
		if (set !== null) {
			Scopes.scopeFor(set)
		}
	}
}