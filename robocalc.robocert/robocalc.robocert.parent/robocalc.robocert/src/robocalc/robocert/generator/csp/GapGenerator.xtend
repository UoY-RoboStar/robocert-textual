package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.SequenceAction
import com.google.common.collect.Iterators
import java.util.Iterator
import java.util.Collections
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.ArrowMessageSpec
import robocalc.robocert.generator.utils.MessageSetOptimiser

/**
 * Generates CSP for the gaps between actions.
 */
class GapGenerator {
	@Inject extension MessageSetOptimiser
	@Inject extension MessageSetGenerator
	@Inject extension MessageSpecGenerator

	/**
	 * Generates CSP for a gap.
	 * 
	 * @param it      the gap.
	 * @param action  the action adjacent to the gap.
	 * 
	 * @return the generated CSP.
	 */
	def generate(SequenceGap it, SequenceAction action) '''«IF isActive»gap(«generateAllowSet», «action.generateActionSet») /\ «ENDIF»'''

	private def generateAllowSet(SequenceGap it) {
		it.allowed = it.allowed.optimise
		it.allowed.generate
	}

	/**
	 * Generates a CSP event set for an action.
	 * 
	 * This is to avoid the possibility of both the gap and the action
	 * accepting the same events.
	 * 
	 * Eventually we'd like to roll this into the main allow set generation,
	 * but some issues with making sure the resulting set 
	 * 
	 * @param it  the gap for which we are generating CSP.
	 * @param action  the action after the gap.
	 * 
	 * @return the generated CSP sequence.
	 */
	private def generateActionSet(SequenceAction action)
	'''{|«FOR i : action.messageSpecs.toIterable SEPARATOR ','»«i.generateCSPEventSet»«ENDFOR»|}'''

	
	private def dispatch Iterator<ArrowMessageSpec> messageSpecs(ArrowAction it) {
		Iterators.singletonIterator(body)
	}

	private def dispatch Iterator<ArrowMessageSpec> messageSpecs(SequenceAction it) {
		Collections.emptyIterator
	}
}
