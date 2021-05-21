package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.SequenceAssertionBody
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.model.robocert.AssertionBody
import robocalc.robocert.model.robocert.WitnessingSequenceAssertionBody

/**
 * Generates CSP for assertions.
 */
class AssertionGenerator {
	// TODO: inject this
	extension TargetGenerator tg = new TargetGenerator();
	
	/**
	 * @return generated CSP for the assertion.
	 */
	def String generate(Assertion assertion) {
		'''
			-- Assertion «assertion.name»
			«assertion.body.generateBody»
		'''
	}

	/**
	 * @return generated CSP for one sequence assertion body.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def dispatch String generateBody(SequenceAssertionBody asst) {
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
	def dispatch String generateBody(AssertionBody asst) {
		""
	}

	/**
	 * Generates CSP for the left-hand side of a witnessing assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	def dispatch String generateLeft(WitnessingSequenceAssertionBody asst) {
		asst.generateTarget
	}

	/**
	 * Generates catch-all CSP for an unsupported assertion's left side.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	def dispatch String generateLeft(SequenceAssertionBody asst) {
		'''{- UNSUPPORTED LHS: «asst» -} STOP'''
	}

	/**
	 * Generates CSP for the right-hand side of a witnessing assertion.
	 * 
	 * Depending on the assertion type, this may expand to the sequence or the
	 * target of the sequence.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the right-hand side of the assertion.
	 */
	def dispatch String generateRight(WitnessingSequenceAssertionBody asst) {
		asst.generateSeqRef
	}

	/**
	 * Generates catch-all CSP for an unsupported assertion's right side.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	def dispatch String generateRight(SequenceAssertionBody asst) {
		'''{- UNSUPPORTED RHS: «asst» -} STOP'''
	}

	/**
	 * @return generated CSP for a sequence reference in one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def String generateSeqRef(SequenceAssertionBody asst) {
		asst.sequence.name
	}

	/**
	 * @return generated CSP for the target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def String generateTarget(SequenceAssertionBody asst) {
		asst.sequence.target.generate
	}

	/**
	 * @return the appropriate FDR model shorthand for this assertion.
	 */
	def String generateModel(AssertionBody asst) {
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