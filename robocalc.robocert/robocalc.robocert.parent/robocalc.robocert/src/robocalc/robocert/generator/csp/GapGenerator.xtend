package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.GapMessageSet
import robocalc.robocert.model.robocert.ExtensionalGapMessageSet
import robocalc.robocert.model.robocert.UniverseGapMessageSet
import robocalc.robocert.generator.utils.GapExtensions
import com.google.common.collect.Iterators

/**
 * Generates CSP for the gaps between actions.
 */
class GapGenerator {
	@Inject extension ActionCSPEventSetGenerator
	@Inject extension MessageSpecGenerator
	@Inject extension GapExtensions

	/**
	 * Generates CSP for a gap.
	 * 
	 * @param it      the gap.
	 * @param action  the action adjacent to the gap.
	 * 
	 * @return the generated CSP.
	 */
	def generate(SequenceGap it, SequenceAction action) '''
	«IF allowsMessages»RUN(
		«generateEventSet(action)»
	) /\ «ENDIF»'''

	private def generateEventSet(SequenceGap it, SequenceAction action) '''
		«IF hasForbidSet(action)»
			diff(«allowed.generateSet», «generateForbidSet(action)»)
		«ELSE»
			«allowed.generateSet»
		«ENDIF»
	'''

	/**
	 * Does this sequence gap need to exclude messages when preceding the
	 * given action?
	 * 
	 * @param it      the sequence gap in question.
	 * @param action  the action after the gap.
	 * 
	 * @return true if, and only if, there is at least one message in the
	 *         forbidden set or one event in the action's CSP events.
	 */
	private def hasForbidSet(SequenceGap it, SequenceAction action) {
		forbidden.hasMessages || action.hasCSPEvents
	}

	/**
	 * Generates a CSP event set for a gap's forbid set.
	 * 
	 * Forbid sets also include any CSP events that the adjacent action can
	 * accept.  This is to avoid the possibility of both the gap and the action
	 * accepting the same events.
	 * 
	 * NOTE: we may need to change this if we ever generalise gap message sets
	 * away from being extensional.
	 */
	private def generateForbidSet(SequenceGap it,
		SequenceAction action) {
		constructSet(generateForbidSetElements(action))
	}

	private def generateForbidSetElements(SequenceGap it, SequenceAction action) {
		Iterators.concat(
			forbidden.messages.iterator.map[generateCSPEventSet],
			action.generateCSPEventSet
		).toIterable
	}

	/**
	 * Generates a CSP event set for an extensional gap message set.
	 * 
	 * @param set  the message set for which we are generating CSP.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	private def dispatch generateSet(ExtensionalGapMessageSet it) {
		constructSet(messages.map[generateCSPEventSet])
	}

	/**
	 * Generates a CSP event set for a universe gap message set.
	 * 
	 * @param set  the message set for which we are generating CSP.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	private def dispatch generateSet(UniverseGapMessageSet it) '''Events'''

	/**
	 * Fallback for generating an event set for an unknown gap message set.
	 * 
	 * @param it  the message set.
	 * 
	 * @return generated CSP for the gap message set (less the set delimiters).
	 */
	private def dispatch generateSet(GapMessageSet it) '''{- UNKNOWN GAP MESSAGE SET: «it» -}'''

	/**
	 * Wraps a pre-generated CSP set in the appropriate delimiters.
	 * 
	 * @param it  the iterable yielding the set for which we are generating CSP.
	 * 
	 * @return generated CSP for the set.
	 */
	private def constructSet(Iterable<CharSequence> it) '''«FOR g : it BEFORE '{|' SEPARATOR ', ' AFTER '|}'»«g»«ENDFOR»'''
}
