package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.GapMessageSpec
import robocalc.robocert.model.robocert.Argument
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator
import robocalc.robocert.model.robocert.ExpressionArgument
import robocalc.robocert.model.robocert.ArrowMessageSpec
import java.util.Collections
import java.util.List
import robocalc.robocert.model.robocert.RestArgument
import robocalc.robocert.generator.utils.TopicExtensions
import robocalc.robocert.generator.utils.TargetExtensions
import robocalc.robocert.model.robocert.MessageDirection

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
		'''«generateHeader»«argumentsIfAny.generateArguments»'''

	/**
	 * Generates a CSP prefix for one sequence arrow action.
	 * 
	 * @param spec  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the message spec.
	 */
	def generatePrefix(ArrowMessageSpec it)
		'''«generateHeader»«arguments.generateArguments»«generateFiller»'''
	
	private def generateHeader(MessageSpec it)
		'''«target.namespace»::«topic.generate»«IF topic.hasDirection».«direction.cspDir»«ENDIF»'''

	
	private def<T extends Argument> generateArguments(Iterable<T> it)
		'''«FOR x: it»«x.generateArgument»«ENDFOR»'''

	/**
	 * Generates any prefix '?_' padding induced by a rest argument.
	 */	
	private def generateFiller(ArrowMessageSpec it)
		'''«FOR x: parametersToFill.toIterable»?_«ENDFOR»'''
	
	private def parametersToFill(ArrowMessageSpec it) {
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
		
	/**
	 * Gets the arguments of this gap message spec in a way that erases
	 * any gap-specific typing information.
	 * 
	 * @param it  the message spec.
	 * 
	 * @return a list of arguments.
	 */
	private def dispatch List<Argument> getArgumentsIfAny(GapMessageSpec it) {
		// TODO(@MattWindsor91): move to metamodel
		arguments.map[it]
	}

	/**
	 * Gets the arguments of this arrow message spec in a way that erases
	 * any arrow-specific typing information.
	 * 
	 * @param it  the message spec.
	 * 
	 * @return a list of arguments.
	 */	
	private def dispatch getArgumentsIfAny(ArrowMessageSpec it) {
		// TODO(@MattWindsor91): move to metamodel
		arguments.map[it]
	}
	
	/**
	 * Gets the arguments of this message spec in a way that erases
	 * any gap-specific typing information.
	 * 
	 * @param it  the message spec.
	 * 
	 * @return an empty list of arguments.
	 */	
	private def dispatch getArgumentsIfAny(MessageSpec it) {
		Collections.emptyList
	}
		
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
