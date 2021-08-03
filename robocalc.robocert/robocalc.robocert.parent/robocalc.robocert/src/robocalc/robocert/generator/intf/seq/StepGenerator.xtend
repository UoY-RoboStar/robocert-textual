package robocalc.robocert.generator.intf.seq

import robocalc.robocert.model.robocert.SequenceStep

/**
 * Any generator for sequence steps.
 */
interface StepGenerator {
	/**
	 * Generates code for a sequence step.
	 * 
	 * @param it  the sequence step.
	 * 
	 * @return a code character sequence.
	 */
	def CharSequence generate(SequenceStep it);	
}
