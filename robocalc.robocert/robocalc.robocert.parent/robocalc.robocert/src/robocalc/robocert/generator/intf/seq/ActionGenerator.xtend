package robocalc.robocert.generator.intf.seq

import robocalc.robocert.model.robocert.SequenceAction

/**
 * Interface for things that generate code for actions.
 */
interface ActionGenerator {
	/**
	 * Generates code for a action.
	 * 
	 * @param it  the action.
	 * 
	 * @return the generated code.
	 */
	def CharSequence generate(SequenceAction it)
}
