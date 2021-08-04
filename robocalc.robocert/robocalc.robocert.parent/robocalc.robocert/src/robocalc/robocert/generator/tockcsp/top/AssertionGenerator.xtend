package robocalc.robocert.generator.tockcsp.top

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceAssertion
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.generator.tockcsp.seq.SequenceGenerator
import robocalc.robocert.generator.tockcsp.seq.TargetGenerator

/**
 * Generates CSP for assertions.
 */
class AssertionGenerator {
	@Inject extension TargetGenerator;
	@Inject extension SequenceGenerator;

	/**
	 * @return generated CSP for the assertion.
	 */
	def CharSequence generate(Assertion it) '''
		-- Assertion «name»
		«generateBody»
	'''

	/**
	 * @return generated CSP for one sequence assertion body.
	 * 
	 * @param it  the assertion for which we are generating CSP.
	 */
	private def dispatch generateBody(
		SequenceAssertion it) '''assert«IF isNegated» not«ENDIF» «generateLeft» [T= «generateRight»«generateTauPriority»'''

	/**
	 * Catch-all case for when we are asked to generate CSP for an assertion
	 * that can't have CSP generated for it.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * @return generated CSP for one sequence assertion body.
	 */
	private def dispatch generateBody(Assertion asst) '''-- skipped (not a CSP assertion)'''

	/**
	 * Generates CSP for the left-hand side of an assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	private def generateLeft(SequenceAssertion it) {
		switch type {
			case HOLDS:
				generateSeqRef
			case IS_OBSERVED:
				generateTarget
			default: '''{- UNSUPPORTED LHS: «type» -} STOP'''
		}
	}

	/**
	 * Generates CSP for the right-hand side of an assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the right-hand side of the assertion.
	 */
	private def generateRight(SequenceAssertion it) {
		switch type {
			case HOLDS:
				generateTarget
			case IS_OBSERVED:
				generateSeqRef
			default: '''{- UNSUPPORTED RHS: «type» -} STOP'''
		}
	}

	/**
	 * @return generated CSP for a sequence reference in one assertion.
	 * 
	 * @param it  the assertion for which we are generating CSP.
	 */
	private def generateSeqRef(SequenceAssertion it) '''«sequence.generateName»'''

	/**
	 * @return generated CSP for the target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def generateTarget(SequenceAssertion it) {
		// TODO(@MattWindsor91): add context for tick-tock (also to seqref)
		instantiated ? generateInstantiatedTarget : generateStandardTarget
	}

	/**
	 * @return generated CSP for the standard target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def generateStandardTarget(SequenceAssertion it) {
		sequence.target.generateClosedTargetRef
	}

	/**
	 * Checks whether this sequence assertion has instantiations.
	 */
	private def isInstantiated(SequenceAssertion it) {
		!instantiation.assignments.empty
	}

	private def generateInstantiatedTarget(SequenceAssertion it) {
		sequence.target.generateOpenTargetRef(instantiation)
	}

	/**
	 * @return the appropriate FDR tau priority pragma for this assertion.
	 */
	private def generateTauPriority(SequenceAssertion it) {
		// TODO(@MattWindsor91): don't do this in tick-tock?
		''' :[tau priority]: {tock}'''
	}
}