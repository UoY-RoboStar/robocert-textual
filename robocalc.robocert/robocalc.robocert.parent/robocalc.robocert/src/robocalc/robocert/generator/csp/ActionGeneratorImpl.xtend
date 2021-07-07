package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.FinalAction
import robocalc.robocert.model.robocert.LoopAction

/**
 * Top-level CSP generator for sequence actions.
 */
class ActionGeneratorImpl implements ActionGenerator {
	@Inject extension LoopGenerator
	@Inject extension MessageSpecGenerator

	/**
	 * Generates CSP for an arrow action.
	 * 
	 * @param it  the arrow action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(ArrowAction it) '''«body.generatePrefix» -> SKIP'''

	/**
	 * Generates CSP for a loop action.
	 * 
	 * @param it  the loop action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(LoopAction it) '''«generateLoop»'''

	/**
	 * Generates CSP for a final action.
	 * 
	 * @param it   the final action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(FinalAction it) '''SKIP'''

	/**
	 * Generates fallback CSP for an unsupported action.
	 * 
	 * @param it  the action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(SequenceAction it) '''{- unsupported action: «it» -} STOP'''

}
