package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.Subsequence
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.generator.intf.seq.StepGenerator

/**
 * A generator that emits CSP for sequences and subsequences.
 */
class SubsequenceGeneratorImpl implements SubsequenceGenerator {
	@Inject extension StepGenerator

	/**
	 * Generates CSP for a subsequence.
	 * 
	 * @param it  the step set for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	override CharSequence generate(Subsequence it) '''
		«IF steps.isEmpty»
			SKIP
		«ELSE»
			«FOR step : steps SEPARATOR ';'»
				«step.generate»
			«ENDFOR»
		«ENDIF»
	'''
}
