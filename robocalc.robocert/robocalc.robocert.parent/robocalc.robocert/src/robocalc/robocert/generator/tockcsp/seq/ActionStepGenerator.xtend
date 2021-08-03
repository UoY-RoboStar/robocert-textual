package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import com.google.common.collect.Iterators
import java.util.Iterator
import java.util.Collections
import robocalc.robocert.model.robocert.ActionStep
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.generator.utils.MessageSetOptimiser
import robocalc.robocert.generator.intf.seq.ActionGenerator

/**
 * Generates CSP-M for action steps.
 */
class ActionStepGenerator {
	@Inject extension ActionGenerator
	@Inject extension MessageSetOptimiser
	@Inject extension MessageSetGenerator
	@Inject extension MessageSpecGenerator

	/**
	 * Generates CSP-M for an action step.
	 * 
	 * @param it  the action step.
	 * 
	 * @return the generated CSP-M.
	 */
	def generateActionStep(ActionStep it) '''(«IF gap.isActive»«generateGap» /\ «ENDIF»«action.generate»)'''

	/**
	 * Generates CSP-M for an action step gap.
	 * 
	 * @param it  the action step.
	 * 
	 * @return the generated CSP-M.
	 */
	private def generateGap(ActionStep it) '''«GAP_PROC»(«generateGapSet», «action.generateActionSet»)'''

	/**
	 * Optimises the gap set in place, then generates it.
	 * 
	 * We do the optimisation like this to preserve containment information,
	 * so sequence group lookup works.
	 * 
	 * @param it  the gap.
	 * 
	 * @return the generated CSP.
	 */
	private def generateGapSet(ActionStep it) {
		gap = gap.optimise
		gap.generate
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

	
	private def dispatch Iterator<MessageSpec> messageSpecs(ArrowAction it) {
		Iterators.singletonIterator(body)
	}

	private def dispatch Iterator<MessageSpec> messageSpecs(SequenceAction it) {
		Collections.emptyIterator
	}

	/**
	 * Name of the process that implements gaps.
	 */
	static final String GAP_PROC = "gap" // in robocert_defs
}
