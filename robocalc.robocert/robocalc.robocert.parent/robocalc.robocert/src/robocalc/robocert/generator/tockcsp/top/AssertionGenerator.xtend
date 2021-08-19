package robocalc.robocert.generator.tockcsp.top

import com.google.inject.Inject
import robocalc.robocert.generator.tockcsp.ll.CSPPropertyGenerator
import robocalc.robocert.generator.tockcsp.seq.SeqPropertyLowerer
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.model.robocert.CSPRefinementProperty
import robocalc.robocert.model.robocert.SequenceProperty

/**
 * Generates CSP for assertions.
 */
class AssertionGenerator {
	@Inject extension CSPPropertyGenerator
	@Inject extension SeqPropertyLowerer

	// TODO(@MattWindsor91): separate out sequence and CSP generators.
	// TODO(@MattWindsor91): make both sides of a sequence CSPProcessSources, then more directly reduce sequences into CSP refinements.

	/**
	 * @return generated CSP for the assertion.
	 */
	def CharSequence generate(Assertion it) '''
		-- Assertion «name»
		«property.generateBody»
	'''

	/**
	 * @param it  the property for which we are generating CSP.
	 * @return generated CSP for one CSP refinement property.
	 */
	private def dispatch generateBody(CSPRefinementProperty it) {
		generateProperty
	}
	
	/**
	 * @param it  the property for which we are generating CSP.
	 * @return generated CSP for one sequence property.
	 */
	private def dispatch generateBody(SequenceProperty it) {
		lower.generateProperty
	}
}
