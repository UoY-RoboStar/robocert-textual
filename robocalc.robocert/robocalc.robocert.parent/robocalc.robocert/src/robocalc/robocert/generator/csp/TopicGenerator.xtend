package robocalc.robocert.generator.csp

import robocalc.robocert.generator.ArrowDirection
import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.OperationTopic
import robocalc.robocert.model.robocert.MessageTopic

/**
 * Generates CSP for message topics.
 */
class TopicGenerator {
	// TODO: parameters (if they go on topics rather than messages)
	// NOTE: parameters might eventually introduce bindings
	/**
	 * Generates CSP for an event topic.
	 * 
	 * @param topic  the topic for which we are generating CSP.
	 * @param dir    the direction of the message.
	 * @param ns     the namespace of the component to which the sequence is attached.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(EventTopic topic, ArrowDirection dir,
		String ns) '''«ns»::«topic.event.name».«dir»'''

	/**
	 * @return generated CSP for an operation topic.
	 * 
	 * @param topic  the topic for which we are generating CSP.
	 * @param dir    the direction of the message.
	 * @param ns     the namespace of the component to which the sequence is attached.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(OperationTopic topic, ArrowDirection dir,
		String ns) '''«ns»::«topic.operation.name»Call'''

	/**
	 * Fallback for generating a topic when we don't recognise the actors
	 * and topic combination.
	 * 
	 * Getting here suggests validation isn't working properly.
	 * 
	 * @param topic  the topic for which we are generating CSP.
	 * @param from   the from-actor.
	 * @param to     the to-actor.
	 * 
	 * @return generated CSP.
	 */
	def dispatch CharSequence generate(MessageTopic topic, ArrowDirection dir,
		String ns) '''{- unsupported topic: topic=«topic» dir=«dir» -} tock'''
}
