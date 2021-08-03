package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.OperationTopic
import robocalc.robocert.model.robocert.MessageTopic

/**
 * Generates CSP for message topics.
 */
class TopicGenerator {
	/**
	 * Generates CSP for an event topic.
	 * 
	 * @param it      the topic for which we are generating CSP.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(EventTopic it) '''«event.name»'''

	/**
	 * Generates CSP for an operation topic.
	 * 
	 * @param it        the topic for which we are generating CSP.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(OperationTopic it) '''«operation.name»Call'''

	/**
	 * Fallback for generating a topic when we don't recognise the actors
	 * and topic combination.
	 * 
	 * Getting here suggests validation isn't working properly.
	 * 
	 * @param it        the topic for which we are generating CSP.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(MessageTopic it) '''{- unsupported topic: topic=«it» -} tock'''

	/**
	 * Gets whether the CSP semantics of this topic requires an explicit
	 * direction.
	 * 
	 * @param it  the topic to analyse.
	 * 
	 * @return whether the generator should emit a direction for this topic.
	 */
	def boolean hasDirection(MessageTopic it) {
		it instanceof EventTopic
	}
}
