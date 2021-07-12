package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.SequenceStep

/**
 * Any generator for sequence steps.
 */
interface StepGenerator {
	/**
	 * Generates CSP for a sequence step.
	 * 
	 * @param it  the sequence step.
	 * 
	 * @return a CSP character sequence.
	 */
	def CharSequence generate(SequenceStep it);	
}