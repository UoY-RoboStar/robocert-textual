package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.Argument
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.generator.utils.TargetExtensions
import robocalc.robocert.model.robocert.WildcardArgument

/**
 * Generates CSP for various aspects of message specs.
 */
class MessageSpecGenerator {
	@Inject extension TopicGenerator
	@Inject extension TargetExtensions
	@Inject extension ArgumentGenerator

	/**
	 * Generates a CSP prefix for one message spec.
	 * 
	 * @param it  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the message spec.
	 */
	def generatePrefix(MessageSpec it)
		'''«generateChannel»«FOR x: arguments»«x.generateForPrefix»«ENDFOR»'''

	/**
	 * Generates a CSP event set for multiple message specs.
	 * 
	 * @param it  the specs for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of multiple message spec.
	 */
	def generateBulkCSPEventSet(Iterable<MessageSpec> it)
		'''Union({«FOR x: it SEPARATOR ','»«x.generateCSPEventSet»«ENDFOR»})'''

	// TODO(@MattWindsor91): optimise the above

	/**
	 * Generates a CSP event set for a message spec.
	 * 
	 * @param it  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one message spec.
	 */
	def generateCSPEventSet(MessageSpec it) {
		val wcs = wildcards
		if (wcs.empty) {
			'''{| «generatePrefix» |}'''
		} else {
			'''{ «generateCSPEventSetComprehensionLHS» | «topic.generateRanges(wcs)» }'''
		}
	}

	private def generateCSPEventSetComprehensionLHS(MessageSpec it) '''
		«generateChannel»«FOR x: arguments.indexed»«x.value.generateForSet(x.key)»«ENDFOR»
	'''

	// TODO(@MattWindsor91): reimplement filler, simplifications

	private def Iterable<Integer> wildcards(MessageSpec it) {
		arguments.indexed.filter[value.binding].map[key]
	}
	
	// TODO(@MattWindsor91): move to metamodel
	private def isBinding(Argument it) {
		// for now
		it instanceof WildcardArgument
	}
	
	/**
	 * Generates the main message channel for a message spec.
	 * 
	 * This needs to be extended with the arguments for a prefix, and
	 * lifted into a set comprehension for an event set.
	 */
	private def generateChannel(MessageSpec it)
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
