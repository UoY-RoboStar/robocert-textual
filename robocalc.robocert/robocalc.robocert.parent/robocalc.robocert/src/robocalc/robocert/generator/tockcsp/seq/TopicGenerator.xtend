package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.OperationTopic
import robocalc.robocert.model.robocert.MessageTopic
import com.google.inject.Inject
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator
import circus.robocalc.robochart.Type
import robocalc.robocert.model.robocert.WildcardArgument

/**
 * Generates CSP for message topics.
 */
class TopicGenerator {
	@Inject extension TypeGenerator
	@Inject extension ArgumentGenerator
	@Inject extension UnsupportedSubclassHandler
	
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
	def dispatch CharSequence generate(MessageTopic it) {
		unsupported("topic", "tock")
	}

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
	
	
	def generateRanges(MessageTopic it, Iterable<Pair<Integer, WildcardArgument>> args)
	    '''«FOR p : args SEPARATOR ', '»
			«comprehensionVar(p.value?.name, p.key)» <- «paramTypeAt(p.key)?.compileType ?: "{- missing type -} int"»
		«ENDFOR»'''
	
	private def dispatch Type paramTypeAt(EventTopic it, int index) {
		index == 0 ? event.type : null
	}
	
	private def dispatch Type paramTypeAt(OperationTopic it, int index) {
		val it = operation.parameters
		index < size ? get(index).type : null
	}
	
	private def dispatch Type paramTypeAt(MessageTopic it, int index) {
		null
	}
}
