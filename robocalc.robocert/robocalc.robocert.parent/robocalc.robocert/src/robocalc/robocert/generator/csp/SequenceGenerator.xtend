package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceStep
import com.google.inject.Inject

/**
 * A generator that emits untimed CSP for a sequence.
 */
class SequenceGenerator {
	@Inject extension ActionGenerator ag
	@Inject extension GapGenerator gg

	// TODO: handle timed vs untimed CSP
	// TODO: consider moving some of the extension methods into the model
	/**
	 * Generates CSP for a sequence.
	 * 
	 * @param sequence  the sequence for which we are generating CSP.
	 * 
	 * @return CSP for this generator's sequence.
	 */
	def CharSequence generateSequence(Sequence sequence) '''
		«sequence.name» =
			«sequence.steps.generateSteps»
	'''

	/**
	 * Generates CSP for a sequential composition of steps.
	 * 
	 * @param steps   the step set for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	private def generateSteps(Iterable<SequenceStep> steps) '''
		«FOR step : steps SEPARATOR ';'»
			«step.generateStep»
		«ENDFOR»
	'''

	/**
	 * @param step   the step for which we are generating CSP.
	 * @return generated CSP for one sequence step.
	 */
	private def generateStep(SequenceStep step) '''(«step.gap.generate(step.action)»«step.action.generate»)'''

}
