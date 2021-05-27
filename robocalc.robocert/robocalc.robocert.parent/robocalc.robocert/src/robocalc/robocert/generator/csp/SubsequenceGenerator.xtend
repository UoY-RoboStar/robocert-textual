package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Subsequence

/**
 * Interface for things that generate CSP for subsequences.
 * 
 * Subsequences induce cyclic dependencies, so the main purpose of this
 * generator is to help break up the dependency cycle.
 */
interface SubsequenceGenerator {
	/**
	 * Generates CSP for a subsequence.
	 * 
	 * @param it  the action.
	 * 
	 * @return the generated CSP.
	 */
	def CharSequence generate(Subsequence it)
}