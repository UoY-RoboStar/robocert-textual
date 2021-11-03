package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.BranchStep
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import com.google.inject.Inject
import robocalc.robocert.model.robocert.AlternativeStep
import robocalc.robocert.model.robocert.Temperature
import robocalc.robocert.model.robocert.InterleaveStep

/**
 * Generator for BranchSteps.
 */
class BranchStepGenerator {
	@Inject extension BranchGenerator
	@Inject extension UnsupportedSubclassHandler
	
	/**
	 * Generates CSP-M for a BranchStep.
	 * 
	 * @param it  the BranchStep to generate.
	 * 
	 * @return  the generated CSP-M process.
	 */
	def CharSequence generate(BranchStep it) '''
	{- «comment» -} (
		«FOR branch : branches SEPARATOR operator»(
			«branch.generate»
		)«ENDFOR»
	)'''
	
	/**
	 * Gets a debug comment corresponding to the branch step.
	 * 
	 * @param it the step to generate.
	 * 
	 * @return the comment.
	 */
	private def comment(BranchStep it) {
		switch it {
			InterleaveStep: "interleave"
			AlternativeStep: '''alternative («temperature»)'''
			default: unsupported("branch step", "?")
		}
	}
	
	/**
	 * Gets the CSP-M operator corresponding to the branch step.
	 * 
	 * @param it the step to generate.
	 * 
	 * @return the corresponding CSP-M.
	 */
	private def operator(BranchStep it) {
		switch it {
			InterleaveStep: INTERLEAVE
			AlternativeStep: altOperator(temperature)
			default: unsupported("branch step", INT_CHOICE)
		}
	}
	
	/**
	 * Expands to the CSP operator for joining together branches on an
	 * alternative branch step.
	 * 
	 * @param it  the temperature of the alternative step to generate.
	 * 
	 * @return  CSP-M external choice if the step is hot; internal otherwise.
	 */
	private def altOperator(Temperature it) {
		switch it {
			case Temperature::COLD: INT_CHOICE
			case Temperature::HOT: EXT_CHOICE
			default: "?"
		}
	}

	/**
	 * The CSP-M external choice operator.
	 */
	static val EXT_CHOICE = "[]"
	
	/**
	 * The CSP-M internal choice operator.
	 */
	static val INT_CHOICE = "|~|"
	
	/**
	 * The CSP-M interleave operator.
	 */
	static val INTERLEAVE = "|||"	
}