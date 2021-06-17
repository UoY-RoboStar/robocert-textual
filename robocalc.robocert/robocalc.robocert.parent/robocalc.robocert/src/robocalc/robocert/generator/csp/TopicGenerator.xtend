package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.OperationTopic
import robocalc.robocert.model.robocert.MessageTopic
import robocalc.robocert.utils.MessageAnalysis
import robocalc.robocert.generator.utils.TargetExtensions
import javax.inject.Inject

/**
 * Generates CSP for message topics.
 */
class TopicGenerator {
	@Inject extension TargetExtensions
	
	// TODO: parameters (if they go on topics rather than messages)
	// NOTE: parameters might eventually introduce bindings
	/**
	 * Generates CSP for an event topic.
	 * 
	 * @param it        the topic for which we are generating CSP.
	 * @param analysis  the analysis of the message's target and direction.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(EventTopic it, MessageAnalysis analysis) '''«analysis.namespace»::«event.name».«analysis.cspDirection»'''

	/**
	 * Generates CSP for an operation topic.
	 * 
	 * @param it        the topic for which we are generating CSP.
	 * @param analysis  the analysis of the message's target and direction.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(OperationTopic it, MessageAnalysis analysis) '''«analysis.namespace»::«operation.name»Call'''

	/**
	 * Fallback for generating a topic when we don't recognise the actors
	 * and topic combination.
	 * 
	 * Getting here suggests validation isn't working properly.
	 * 
	 * @param it        the topic for which we are generating CSP.
	 * @param analysis  the analysis of the message's target and direction.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(MessageTopic it, MessageAnalysis analysis) '''{- unsupported topic: topic=«it» -} tock'''
		
	private def getNamespace(MessageAnalysis it) {
		target?.namespace ?: "NO_TARGET"
	}
}
