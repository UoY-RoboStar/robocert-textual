package robocalc.robocert.generator.intf.seq

import robocalc.robocert.model.robocert.Subsequence

/**
 * Interface for things that generate code for subsequences.
 * 
 * Subsequences induce cyclic dependencies, so the main purpose of this
 * generator is to help break up the dependency cycle.
 */
interface SubsequenceGenerator {
	/**
	 * Generates code for a subsequence.
	 * 
	 * @param it  the action.
	 * 
	 * @return the generated code.
	 */
	def CharSequence generate(Subsequence it)
}