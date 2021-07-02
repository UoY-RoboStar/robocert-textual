package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.ExtensionalMessageSet
import com.google.inject.Inject
import robocalc.robocert.model.robocert.UniverseMessageSet
import robocalc.robocert.model.robocert.MessageSet
import com.google.common.collect.Iterables
import robocalc.robocert.model.robocert.RefMessageSet
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.NamedMessageSet
import java.util.Collections

/**
 * CSP generator for message sets.
 */
class MessageSetGenerator {
	@Inject extension MessageSpecGenerator
	
	/**
	 * Generates a CSP event set for an extensional gap message set.
	 * 
	 * @param it     the sequence gap in question.
	 * @param extra  any additional messages to add into the set.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch generate(ExtensionalMessageSet it, Iterable<MessageSpec> extra) {
		constructSet(Iterables.concat(messages, extra))
	}

	/**
	 * Generates a CSP event set for a universe gap message set.
	 * 
	 * @param it     the message set for which we are generating CSP.
	 * @param extra  any extra events to add to the set (ignored here).
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch generate(UniverseMessageSet it, Iterable<MessageSpec> extra) '''Events'''

	/**
	 * Generates a CSP event set for a reference gap message set.
	 * 
	 * @param it     the message set for which we are generating CSP.
	 * @param extra  any extra events to add to the set.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch generate(RefMessageSet it, Iterable<MessageSpec> extra) '''
	«IF extra.empty»
		«set.generateName»
	«ELSE»
		union(«set.generateName», «constructSet(extra)»)
	«ENDIF»
	'''

	/**
	 * Fallback for generating an event set for an unknown gap message set.
	 * 
	 * @param it  the message set.
	 * 
	 * @return generated CSP for the gap message set (less the set delimiters).
	 */
	def dispatch generate(MessageSet it, Iterable<MessageSpec> extra) '''{- UNKNOWN MESSAGE SET: «it» -}'''

	/**
	 * Generates a reference to a named message set.
	 * 
	 * We emit references rather than textual inclusion to make the
	 * generated CSP cleaner and more debuggable.
	 * 
	 * This name will agree with that given in generateNamedSets.
	 * 
	 * @param it  the set to reference.
	 * 
	 */
	def generateName(NamedMessageSet it) '''MsgSets::«name»'''

		
	def generateNamedSets(Iterable<NamedMessageSet> sets) '''
	«IF sets.isNullOrEmpty»
		-- No named message sets
	«ELSE»
		-- Named message sets
		module MsgSets
		exports
			«FOR set: sets»
				«IF set.set !== null»
					«set.name» = «set.set.generate(Collections.emptyList)»
				«ENDIF»
			«ENDFOR»
		endmodule
	«ENDIF»
	'''

	/**
	 * Constructs a CSP enumerated set from an iterable of message specs.
	 * 
	 * @param it  the iterable yielding the set for which we are generating CSP.
	 * 
	 * @return generated CSP for the set.
	 */
	private def constructSet(
		Iterable<MessageSpec> it) '''«FOR g : it BEFORE '{|' SEPARATOR ', ' AFTER '|}'»«g.generateCSPEventSet»«ENDFOR»'''
}