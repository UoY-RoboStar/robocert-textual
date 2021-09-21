package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.BranchStep
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import com.google.inject.Inject
import robocalc.robocert.model.robocert.Subsequence
import robocalc.robocert.model.robocert.AlternativeStep
import robocalc.robocert.model.robocert.Temperature
import robocalc.robocert.model.robocert.InterleaveStep

/**
 * Generator for BranchSteps.
 */
class BranchStepGenerator {
	@Inject extension SubsequenceGenerator
	@Inject extension UnsupportedSubclassHandler
	
	/**
	 * Generates CSP-M for a BranchStep.
	 * 
	 * @param it  the BranchStep to generate.
	 * 
	 * @return  the generated CSP-M process.
	 */
	def CharSequence generateBranch(BranchStep it) {
		generateJoin(branches, operator)
	}
	
	private def generateJoin(Iterable<Subsequence> branches, CharSequence op) '''
		«FOR branch : branches SEPARATOR op»
			(
				«branch.generate»
			)
		«ENDFOR»
	'''
	
	/**
	 * Expands to the CSP operator for joining together branches on an
	 * alternative branch step.
	 * 
	 * @param it  the AlternativeStep to generate.
	 * 
	 * @return  CSP-M external choice if the step is hot; internal otherwise.
	 */
	private def dispatch operator(AlternativeStep it) {
		switch temperature {
			case Temperature::COLD: INT_CHOICE
			case Temperature::HOT: EXT_CHOICE
			default: unsupported("temperature", INT_CHOICE)
		}
	}
	
	/**
	 * Fallback CSP operator for joining together branches on an
	 * interleave.
	 * 
	 * @param it  the InterleaveStep to generate.
	 * 
	 * @return  an interleave operator.
	 */
	private def dispatch operator(InterleaveStep it) {
		INTERLEAVE
	}
	
	/**
	 * Fallback CSP operator for joining together branches on an
	 * unsupported branch step.
	 * 
	 * @param it  the BranchStep to generate.
	 * 
	 * @return  a fallback operator.
	 */
	private def dispatch operator(BranchStep it) {
		unsupported("branch step", INT_CHOICE)
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