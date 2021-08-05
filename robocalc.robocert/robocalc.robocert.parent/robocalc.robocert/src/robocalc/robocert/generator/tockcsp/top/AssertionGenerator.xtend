package robocalc.robocert.generator.tockcsp.top

import com.google.inject.Inject
import robocalc.robocert.model.robocert.CSPRefinementProperty
import robocalc.robocert.model.robocert.SequenceProperty
import robocalc.robocert.model.robocert.Property
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.generator.tockcsp.seq.SequenceGenerator
import robocalc.robocert.generator.tockcsp.seq.TargetGenerator
import robocalc.robocert.model.robocert.CSPModel
import robocalc.robocert.model.robocert.CSPProcessSource
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.ProcessCSPFragment

/**
 * Generates CSP for assertions.
 */
class AssertionGenerator {
	@Inject extension TargetGenerator;
	@Inject extension SequenceGenerator;

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
	 * @return generated CSP for one CSP refinement property.
	 * 
	 * @param it  the property for which we are generating CSP.
	 */
	private def dispatch generateBody(
		CSPRefinementProperty it) '''«generateRefinement(lhs.name, rhs.name, model)»'''

	private def dispatch getName(Sequence it) {
		generateName
	}

	private def dispatch getName(ProcessCSPFragment it) { name }

	private def dispatch getName(CSPProcessSource it) '''???'''
	/**
	 * @return generated CSP for one sequence property.
	 * 
	 * @param it  the property for which we are generating CSP.
	 */
	private def dispatch generateBody(
		SequenceProperty it) '''«generateRefinement(generateLeft, generateRight, model)»'''

	/**
	 * Catch-all case for when we are asked to generate CSP for an assertion
	 * that can't have CSP generated for it.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * @return generated CSP for one sequence assertion body.
	 */
	private def dispatch generateBody(Assertion asst) '''-- skipped (not a CSP assertion)'''

	private def generateRefinement(Property it, CharSequence lhs, CharSequence rhs, CSPModel model)	'''
		assert«IF isNegated» not«ENDIF» «lhs» [T= «rhs»«model.generateTauPriority»
	'''

	/**
	 * Generates CSP for the left-hand side of an assertion.
	 * 
	 * @param it  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	private def generateLeft(SequenceProperty it) {
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
	private def generateRight(SequenceProperty it) {
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
	private def generateSeqRef(SequenceProperty it) '''«sequence.generateName»'''

	/**
	 * @return generated CSP for the target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def generateTarget(SequenceProperty it) {
		// TODO(@MattWindsor91): add context for tick-tock (also to seqref)
		instantiated ? generateInstantiatedTarget : generateStandardTarget
	}

	/**
	 * @return generated CSP for the standard target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	private def generateStandardTarget(SequenceProperty it) {
		sequence.target.generateClosedTargetRef
	}

	/**
	 * Checks whether this sequence assertion has instantiations.
	 */
	private def isInstantiated(SequenceProperty it) {
		!instantiation.assignments.empty
	}

	private def generateInstantiatedTarget(SequenceProperty it) {
		sequence.target.generateOpenTargetRef(instantiation)
	}

	/**
	 * @return the appropriate FDR tau priority pragma for this model.
	 */
	private def generateTauPriority(CSPModel it) {
		// TODO(@MattWindsor91): don't do this in tick-tock?
		''' :[tau priority]: {tock}'''
	}
}
