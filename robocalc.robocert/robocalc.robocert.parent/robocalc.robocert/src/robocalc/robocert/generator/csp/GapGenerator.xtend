package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.StrictGap
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.LooseGap
import robocalc.robocert.model.robocert.GapMessageSet

/**
 * Generates CSP for the gaps between actions.
 */
class GapGenerator {
	@Inject extension ActionGenerator ag
	@Inject extension MessageSpecGenerator mg

	/**
	 * Generates CSP for a strict gap.
	 * 
	 * A strict gap has no CSP emitted, so this just returns the empty string.
	 * 
	 * @param gap     the strict gap.
	 * @param action  the action adjacent to the gap (ignored)
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(StrictGap gap, SequenceAction action) ''''''

	/**
	 * Generates CSP for a loose gap.
	 * 
	 * @param gap     the loose gap.
	 * @param action  the action adjacent to the gap, used for calculating
	 *                events set to remove from the gap process.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(LooseGap gap, SequenceAction action) '''
		RUN(
			«gap.generateLooseSet(action)»
		) /\ '''

	/**
	 * Generates fallback CSP for an unsupported gap.
	 * 
	 * @param gap     the gap.
	 * @param action  the action adjacent to the gap.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(SequenceGap gap, SequenceAction action) '''{- unsupported gap: «gap» -} '''

	private def generateLooseSet(LooseGap gap, SequenceAction action) '''
	«IF gap.forbidden.present || action.hasCSPEvents»
		diff(«gap.allowed.generateAllowSet», «gap.forbidden.generateForbidSet(action)»)
	«ELSE»
		«gap.allowed.generateAllowSet»
	«ENDIF»
	'''

	/**
	 * Generates a CSP event set for an allow-set.
	 * 
	 * Allow sets are unusual in that the empty allow set is special-cased as
	 * 'Events', ie the universal set.  This may change in future.
	 * 
	 * @param set  the allow-set.
	 * 
	 * @return the generated CSP.
	 */
	private def generateAllowSet(GapMessageSet set) '''«IF set.present»{|«set.generateSet»|}«ELSE»Events«ENDIF»'''

	/**
	 * Generates a CSP event set for a forbid-set.
	 * 
	 * Forbid sets also include any CSP events that the adjacent action can
	 * accept.  This is to avoid the possibility of both the gap and the action
	 * accepting the same events.
	 * 
	 * NOTE: we may need to change this if we ever generalise gap message sets
	 * away from being extensional.
	 */
	private def generateForbidSet(GapMessageSet set,
		SequenceAction action) '''{|«set.generateSet»«IF action.hasCSPEvents», «action.generateCSPEventSet»«ENDIF»|}'''

	/**
	 * Generates a CSP event set for a gap message set.
	 * 
	 * @param set  the message set for which we are generating CSP.
	 * 
	 * @return generated CSP for the gap message set, (less the set delimiters).
	 */
	private def generateSet(
		GapMessageSet set) '''«FOR m : set.messages SEPARATOR ', '»«m.generateCSPEventSet»«ENDFOR»'''

	/**
	 * @return whether this message set is present (non-null and has messages).
	 */
	private def isPresent(GapMessageSet set) {
		// TODO: check if this is actually necessary
		// TODO: move to metamodel?
		set !== null && !set.messages.isNullOrEmpty
	}

}
