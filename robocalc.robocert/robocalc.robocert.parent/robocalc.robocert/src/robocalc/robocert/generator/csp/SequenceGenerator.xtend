package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceStep
import com.google.inject.Inject
import robocalc.robocert.model.robocert.Subsequence

/**
 * A generator that emits untimed CSP for sequences and subsequences.
 */
class SequenceGenerator implements SubsequenceGenerator {
	@Inject extension ActionGenerator
	@Inject extension GapGenerator
	@Inject extension TargetGenerator

	// TODO: handle timed vs untimed CSP
	// TODO: consider moving some of the extension methods into the model
	/**
	 * Generates CSP for a sequence.
	 * 
	 * @param it  the sequence for which we are generating CSP.
	 * 
	 * @return CSP for this generator's sequence.
	 */
	def CharSequence generate(Sequence it) '''
		module «name»
		exports
		Timed(OneStep) {
			Sequence =
				«body.generate»

			«target.generateOpenTargetDef»

			«target.generateClosedTargetDef»
		}
		endmodule
	'''
	
	/**
	 * Generates CSP for a subsequence.
	 * 
	 * @param it  the step set for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	override CharSequence generate(Subsequence it) '''
		«FOR step : steps SEPARATOR ';'»
			«step.generateStep»
		«ENDFOR»
	'''

	/**
	 * Generates CSP for one sequence step.
	 * 
	 * @param it  the step for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	private def generateStep(SequenceStep it) '''(«gap.generate(action)»«action.generate»)'''
}
