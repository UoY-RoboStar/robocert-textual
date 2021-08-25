package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.model.robocert.CSPRefinementOperator
import robocalc.robocert.model.robocert.SequenceProperty
import com.google.inject.Inject
import robocalc.robocert.model.robocert.CSPRefinementProperty

/**
 * Lowers sequence properties into CSP refinement ones.
 */
class SeqPropertyLowerer {
	@Inject RobocertFactory rf
		
	/**
	 * Lowers a sequence property into a CSP refinement one.
	 *
	 * All sequence properties (thus far) are single direction refinements,
	 * where the LHS and RHS depend on the sequence property operator.
	 * 
	 * @param p  the property to lower.
	 * 
	 * @return  the lowered property.
	 */
	 def CSPRefinementProperty lower(SequenceProperty p) {
		rf.createCSPRefinementProperty=>[
			lhs = p.lhs
			rhs = p.rhs
			type = CSPRefinementOperator::REFINES
			// TODO(@MattWindsor91): may change according to GitHub #56
			model = p.model
		]
	}
	
	/**
	 * Gets the appropriate refinement left-hand side for this sequence property.
	 * 
	 * @param it  the property for which we are generating CSP.
	 * 
	 * @return the left-hand side process source.
	 */
	private def getLhs(SequenceProperty it) {
		switch type {
			case HOLDS:
				sequence
			case IS_OBSERVED:
				target
		}
	}

	/**
	 * Gets the appropriate refinement left-hand side for this sequence property.
	 * 
	 * @param it  the property for which we are generating CSP.
	 * 
	 * @return the left-hand side process source.
	 */
	private def getRhs(SequenceProperty it) {
		// This should be the mirror image of getLhs, typically.
		switch type {
			case HOLDS:
				target
			case IS_OBSERVED:
				sequence
		}
	}

	/**
	 * @param it  the sequence property for which we are getting the target
	 *
	 * @return the target of the sequence property.
	 * 
	 */
	private def getTarget(SequenceProperty it) {
		// TODO(@MattWindsor91): reinstate instantiation
		// instantiated ? generateInstantiatedTarget : generateStandardTarget
		sequence?.target
	}
}