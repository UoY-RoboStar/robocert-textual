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
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator

/**
 * Generates CSP-M for action steps.
 */
class ActionStepGenerator {
	@Inject extension LoadStoreGenerator
	@Inject extension ActionGenerator
	@Inject extension MessageSetOptimiser
	@Inject extension MessageSetGenerator
	@Inject extension MessageSpecGenerator

	// This generator handles the injection of loads for any possible
	// expressions in the action, as it is safe to do so at this level (no
	// Action recursively includes any more Steps or Actions).
	//
	// It does *not* handle the injection of stores; we do that in the
	// generator for ArrowActions.

	/**
	 * Generates CSP-M for an action step.
	 * 
	 * @param it  the action step.
	 * 
	 * @return the generated CSP-M.
	 */
	def generateActionStep(ActionStep it)
		'''«generateExpressionLoads»«generateActionStepInner»'''

	/**
	 * Generates CSP-M for the inner (post-load) part of an action step.
	 * 
	 * @param it  the action step.
	 * 
	 * @return the generated CSP-M.
	 */
	private def generateActionStepInner(ActionStep it) '''«generateGap»(«EVENTUALLY_PROC»(«action.generate»))'''

	/**
	 * Generates CSP-M for an action step gap.
	 *
	 * Currently, we generate gaps for all action steps regardless of
	 * whether the gap is active.  This part of the semantics is subject
	 * to change.
	 * 
	 * @param it  the action step.
	 * 
	 * @return the generated CSP-M.
	 */
	private def generateGap(ActionStep it) '''«GAP_PROC»(«generateGapSet», «generateActionSet»)'''

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
	 * Generates the gap action set for an action step.
	 * 
	 * This is to avoid the possibility of both the gap and the action
	 * accepting the same events.
	 * 
	 * @param it  the step for which we are generating CSP.
	 * 
	 * @return the generated CSP sequence.
	 */
	private def generateActionSet(ActionStep it) {
		action.messageSpecs.toList.generateBulkCSPEventSet
	}
	
	private def dispatch Iterator<MessageSpec> messageSpecs(ArrowAction it) {
		Iterators.singletonIterator(body)
	}

	private def dispatch Iterator<MessageSpec> messageSpecs(SequenceAction it) {
		Collections.emptyIterator
	}

	/**
	 * Name of the process that implements eventually-lifts.
	 */
	static val EVENTUALLY_PROC = "Cold" // in robocert_seq_defs

	/**
	 * Name of the process that implements gaps.
	 */
	static val GAP_PROC = "Action" // in robocert_seq_defs
}
