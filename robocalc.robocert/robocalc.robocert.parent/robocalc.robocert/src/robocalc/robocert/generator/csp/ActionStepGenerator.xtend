package robocalc.robocert.generator.csp

import com.google.inject.Inject
import com.google.common.collect.Iterators
import java.util.Iterator
import java.util.Collections
import robocalc.robocert.model.robocert.ActionStep
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.generator.utils.MessageSetOptimiser


/**
 * Generates CSP for action steps.
 */
class ActionStepGenerator {
	@Inject extension ActionGenerator
	@Inject extension MessageSetOptimiser
	@Inject extension MessageSetGenerator
	@Inject extension MessageSpecGenerator

	/**
	 * Generates CSP for an action step.
	 * 
	 * @param it  the action step.
	 * 
	 * @return the generated CSP.
	 */
	def generateActionStep(ActionStep it) '''(«IF gap.isActive»gap(«generateGap», «action.generateActionSet») /\ «ENDIF»«action.generate»)'''

	/**
	 * Optimises the action gap in place, then generates it.
	 * 
	 * We do the optimisation like this to preserve containment information,
	 * so sequence group lookup works.
	 * 
	 * @param it  the gap.
	 * 
	 * @return the generated CSP.
	 */
	private def generateGap(ActionStep it) {
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
}
