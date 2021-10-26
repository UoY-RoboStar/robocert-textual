package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.model.robocert.OperationTopic
import robocalc.robocert.model.robocert.MessageTopic
import com.google.inject.Inject
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator
import circus.robocalc.robochart.Type
import robocalc.robocert.model.robocert.WildcardArgument
import robocalc.robocert.generator.tockcsp.top.BindingGenerator

/**
 * Generates CSP for message topics.
 */
class TopicGenerator {
	@Inject extension TypeGenerator
	@Inject extension BindingGenerator
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
	
	/**
	 * Generates the set comprehension ranges for a set of arguments, using
	 * the given topic to resolve types.
	 * 
	 * @param it  the topic for which we are generating ranges.
	 * 
	 * @param args  an iterable of pairs of index in the message argument list,
	 *              and wildcard argument to expand into a comprehension.
	 * 
	 * @return  CSP-M for the set comprehension, less any set delimiters.
	 */
	def generateRanges(MessageTopic it, Iterable<Pair<Integer, WildcardArgument>> args)
	    '''«FOR p : args SEPARATOR ', '»«generateRange(p.value, p.key)»«ENDFOR»'''
	
	private def generateRange(MessageTopic it, WildcardArgument arg, int index)
		'''«arg.binding.generateArgumentName(index)» <- «paramTypeAt(index)?.compileType ?: "{- missing type -} int"»'''
	
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
