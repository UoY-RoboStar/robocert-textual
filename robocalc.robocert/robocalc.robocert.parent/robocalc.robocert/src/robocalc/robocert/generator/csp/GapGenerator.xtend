package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.SequenceAction
import com.google.common.collect.Iterators
import robocalc.robocert.model.robocert.ExtensionalMessageSet
import robocalc.robocert.model.robocert.UniverseMessageSet
import robocalc.robocert.model.robocert.MessageSet
import java.util.Iterator
import java.util.Collections

/**
 * Generates CSP for the gaps between actions.
 */
class GapGenerator {
	@Inject extension ActionCSPEventSetGenerator
	@Inject extension MessageSpecGenerator

	/**
	 * Generates CSP for a gap.
	 * 
	 * @param it      the gap.
	 * @param action  the action adjacent to the gap.
	 * 
	 * @return the generated CSP.
	 */
	def generate(SequenceGap it, SequenceAction action) '''
	«IF isActive»RUN(
			«generateEventSet(action)»
	) /\ «ENDIF»'''

	private def generateEventSet(SequenceGap it, SequenceAction action) '''
		«IF hasForbidSet(action)»
			diff(«generateAllowSet», «generateForbidSet(action)»)
		«ELSE»
			«generateAllowSet»
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
		forbidden.isActive || action.hasCSPEvents
	}

	/**
	 * Generates a CSP event set for a gap's allow set.
	 * 
	 * @param it  the gap for which we are generating CSP.
	 * 
	 * @return the generated CSP sequence.
	 */
	private def generateAllowSet(SequenceGap it) {
		allowed.generateSet(Collections.emptyIterator)
	}

	/**
	 * Generates a CSP event set for a gap's forbid set.
	 * 
	 * Forbid sets also include any CSP events that the adjacent action can
	 * accept.  This is to avoid the possibility of both the gap and the action
	 * accepting the same events.
	 * 
	 * @param it  the gap for which we are generating CSP.
	 * @param action  the action after the gap.
	 * 
	 * @return the generated CSP sequence.
	 */
	private def generateForbidSet(SequenceGap it, SequenceAction action) {
		forbidden.generateSet(action.generateCSPEventSet)
	}

	/**
	 * Generates a CSP event set for an extensional gap message set.
	 * 
	 * @param it      the sequence gap in question.
	 * @param action  the action after the gap.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	private def dispatch generateSet(ExtensionalMessageSet it, Iterator<CharSequence> extra) {
		constructSet(Iterators.concat(
			messages.iterator.map[generateCSPEventSet],
			extra
		).toIterable)
	}

	/**
	 * Generates a CSP event set for a universe gap message set.
	 * 
	 * @param it     the message set for which we are generating CSP.
	 * @param extra  any extra events to add to the set (ignored here).
	 * 
	 * @return generated CSP for the gap message set.
	 */
	private def dispatch generateSet(UniverseMessageSet it, Iterator<CharSequence> extra) '''Events'''

	/**
	 * Fallback for generating an event set for an unknown gap message set.
	 * 
	 * @param it  the message set.
	 * 
	 * @return generated CSP for the gap message set (less the set delimiters).
	 */
	private def dispatch generateSet(MessageSet it, Iterator<CharSequence> extra) '''{- UNKNOWN MESSAGE SET: «it» -}'''

	/**
	 * Wraps a pre-generated CSP set in the appropriate delimiters.
	 * 
	 * @param it  the iterable yielding the set for which we are generating CSP.
	 * 
	 * @return generated CSP for the set.
	 */
	private def constructSet(
		Iterable<CharSequence> it) '''«FOR g : it BEFORE '{|' SEPARATOR ', ' AFTER '|}'»«g»«ENDFOR»'''
}
