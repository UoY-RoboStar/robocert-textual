package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceStep
import robocalc.robocert.model.robocert.ActionStep
import robocalc.robocert.model.robocert.LoopStep
import robocalc.robocert.model.robocert.DeadlineStep
import robocalc.robocert.generator.intf.seq.StepGenerator

/**
 * Generator for sequence steps.
 * 
 * This generator mainly just delegates into lower-level generators.
 */
class StepGeneratorImpl implements StepGenerator {
	@Inject extension ActionStepGenerator	
	@Inject extension DeadlineGenerator
	@Inject extension LoopGenerator
	
	override generate(SequenceStep it) { generateStep }
	
	/**
	 * Generates CSP for an action step.
	 * 
	 * @param it  the step for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	private def dispatch generateStep(ActionStep it) { generateActionStep }
	

	/**
	 * Generates CSP for a loop step.
	 * 
	 * @param it  the loop step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateStep(LoopStep it) { generateLoop }

	/**
	 * Generates CSP for a deadline step.
	 * 
	 * @param it  the deadline step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateStep(DeadlineStep it) { generateDeadline }	

	/**
	 * Generates CSP for an unknown step.
	 * 
	 * @param it  the step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateStep(SequenceStep it) '''{- UNKNOWN STEP: «it» -} STOP'''
}
