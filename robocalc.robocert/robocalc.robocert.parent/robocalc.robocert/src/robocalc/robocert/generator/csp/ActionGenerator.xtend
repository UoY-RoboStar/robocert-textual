package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.SequenceAction

/**
 * Interface for things that generate CSP for actions.
 */
interface ActionGenerator {
	/**
	 * Generates CSP for a action.
	 * 
	 * @param it  the action.
	 * 
	 * @return the generated CSP.
	 */
	def CharSequence generate(SequenceAction it)
}