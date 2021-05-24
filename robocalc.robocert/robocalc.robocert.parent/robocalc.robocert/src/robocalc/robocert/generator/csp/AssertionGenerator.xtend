package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.SequenceAssertionBody
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.model.robocert.AssertionBody
import robocalc.robocert.model.robocert.ObservedSequenceAssertionBody
import robocalc.robocert.model.robocert.ImplementedSequenceAssertionBody
import com.google.inject.Inject

/**
 * Generates CSP for assertions.
 */
class AssertionGenerator {
	@Inject extension TargetGenerator tg;
	
	/**
	 * @return generated CSP for the assertion.
	 */
	def CharSequence generate(Assertion assertion)
		'''
			-- Assertion «assertion.name»
			«assertion.body.generateBody»
		'''

	/**
	 * @return generated CSP for one sequence assertion body.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def dispatch generateBody(SequenceAssertionBody asst) {
		var lhs = asst.generateLeft;
		var rhs = asst.generateRight;
		var model = asst.generateModel;
		'''
			assert«IF asst.isNegated» not«ENDIF» «lhs» [«model»= «rhs»
		'''
	}

	/**
	 * Catch-all case for when we are asked to generate CSP for an assertion
	 * that can't have CSP generated for it.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * @return generated CSP for one sequence assertion body.
	 */
	private def dispatch generateBody(AssertionBody asst) {
		""
	}

	/**
	 * Generates CSP for the left-hand side of an 'observed' assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	private def dispatch generateLeft(ObservedSequenceAssertionBody asst) {
		asst.generateTarget
	}

	/**
	 * Generates CSP for the left-hand side of an 'implemented' assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	private def dispatch generateLeft(ImplementedSequenceAssertionBody asst) {
		asst.generateSeqRef
	}

	/**
	 * Generates catch-all CSP for an unsupported assertion's left side.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	private def dispatch generateLeft(SequenceAssertionBody asst) {
		'''{- UNSUPPORTED LHS: «asst» -} STOP'''
	}

	/**
	 * Generates CSP for the right-hand side of an 'observed' assertion.
	 * 
	 * Depending on the assertion type, this may expand to the sequence or the
	 * target of the sequence.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the right-hand side of the assertion.
	 */
	private def dispatch generateRight(ObservedSequenceAssertionBody asst) {
		asst.generateSeqRef
	}
	
	/**
	 * Generates CSP for the right-hand side of an 'implemented' assertion.
	 * 
	 * Depending on the assertion type, this may expand to the sequence or the
	 * target of the sequence.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the right-hand side of the assertion.
	 */
	private def dispatch generateRight(ImplementedSequenceAssertionBody asst) {
		asst.generateTarget
	}

	/**
	 * Generates catch-all CSP for an unsupported assertion's right side.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	private def dispatch generateRight(SequenceAssertionBody asst)
		'''{- UNSUPPORTED RHS: «asst» -} STOP'''

	/**
	 * @return generated CSP for a sequence reference in one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def generateSeqRef(SequenceAssertionBody asst) {
		asst.sequence.name
	}

	/**
	 * @return generated CSP for the target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def generateTarget(SequenceAssertionBody asst) {
		asst.sequence.target.generate
	}

	/**
	 * @return the appropriate FDR model shorthand for this assertion.
	 */
	private def generateModel(AssertionBody asst) {
		switch asst.assertion.model {
			case TRACES:
				"T"
			case FAILURES:
				"F"
			case FAILURES_DIVERGENCES:
				"FD"
		}
	}
}