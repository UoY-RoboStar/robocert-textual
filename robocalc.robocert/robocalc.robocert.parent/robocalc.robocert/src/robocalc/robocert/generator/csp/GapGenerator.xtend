package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.SequenceAction
import com.google.common.collect.Iterators
import java.util.Iterator
import java.util.Collections
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.ArrowAction

/**
 * Generates CSP for the gaps between actions.
 */
class GapGenerator {
	@Inject extension MessageSetGenerator

	/**
	 * Generates CSP for a gap.
	 * 
	 * @param it      the gap.
	 * @param action  the action adjacent to the gap.
	 * 
	 * @return the generated CSP.
	 */
	def generate(SequenceGap it, SequenceAction action) '''«IF isActive»gap(«generateAllowSet», «generateForbidSet(action)») /\ «ENDIF»'''

	/**
	 * Generates a CSP event set for a gap's allow set.
	 * 
	 * @param it  the gap for which we are generating CSP.
	 * 
	 * @return the generated CSP sequence.
	 */
	private def generateAllowSet(SequenceGap it) {
		allowed.generate(Collections.emptyList)
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
		forbidden.generate(action.messageSpecs.toList)
	}
	
	private def dispatch Iterator<MessageSpec> messageSpecs(ArrowAction it) {
		Iterators.singletonIterator(body)
	}

	private def dispatch Iterator<MessageSpec> messageSpecs(SequenceAction it) {
		Collections.emptyIterator
	}

}
