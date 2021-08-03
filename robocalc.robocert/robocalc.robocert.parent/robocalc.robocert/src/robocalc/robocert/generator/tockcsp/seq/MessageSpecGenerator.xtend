package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.Argument
import robocalc.robocert.model.robocert.ExpressionArgument
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.RestArgument
import robocalc.robocert.generator.utils.TopicExtensions
import robocalc.robocert.generator.utils.TargetExtensions
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator

/**
 * Generates CSP for various aspects of message specs.
 */
class MessageSpecGenerator {
	@Inject extension TopicExtensions
	@Inject extension TopicGenerator
	@Inject extension TargetExtensions
	@Inject extension ExpressionGenerator

	/**
	 * Generates a CSP event set for a gap message spec (less the set delimiters).
	 * 
	 * @param it  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one message spec.
	 */
	def generateCSPEventSet(MessageSpec it)
		'''«generateHeader»«arguments.generateArguments»'''

	/**
	 * Generates a CSP prefix for one sequence arrow action.
	 * 
	 * @param spec  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the message spec.
	 */
	def generatePrefix(MessageSpec it)
		'''«generateHeader»«arguments.generateArguments»«generateFiller»'''
	
	private def generateHeader(MessageSpec it)
		'''«namespace»::«topic.generate»«IF topic.hasDirection».«direction.cspDir»«ENDIF»'''
	
	private def getNamespace(MessageSpec it) {
		target?.namespace ?: missingNamespace
	}
	
	/**
	 * Expands to a placeholder for a missing namespace.
	 * 
	 * This predominantly exists for debugging purposes.
	 * 
	 * @param it  the message spec whose namespace is missing.
	 * 
	 * @return a placeholder character sequence.
	 */
	private def missingNamespace(MessageSpec it) '''{- missing namespace: «it» -} MISSING'''
	
	private def<T extends Argument> generateArguments(Iterable<T> it)
		'''«FOR x: it»«x.generateArgument»«ENDFOR»'''

	/**
	 * Generates any prefix '?_' padding induced by a rest argument.
	 */	
	private def generateFiller(MessageSpec it)
		'''«FOR x: parametersToFill.toIterable»?_«ENDFOR»'''
	
	private def parametersToFill(MessageSpec it) {
		val amount = arguments.takeWhile[!(it instanceof RestArgument)].length
		topic.params.drop(amount)
	}
	
	/**
	 * Generates an expression argument.
	 * 
	 * Right now, we just generate the expression inline and hope the result
	 * is well-formed CSP.  This will eventually change for non-literal
	 * expressions.
	 */
	private def dispatch generateArgument(ExpressionArgument it)
		'''.«compileExpression(expr, it)»'''
	
	/**
	 * Generates a 'rest' argument.
	 * 
	 * This doesn't actually expand to anything, as we handle filling in
	 * rest arguments separately.
	 */	
	private def dispatch generateArgument(RestArgument it) ''''''
	
	private def dispatch generateArgument(Argument it)
		'''{- UNKNOWN ARGUMENT: «it» -}'''
		
	def private cspDir(MessageDirection it) {
		switch(it) {
			case INBOUND:
				"in"
			case OUTBOUND:
				"out"
			default:
				"??"
		}
	}
}
