package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.Process;
import robocalc.robocert.model.robocert.CSPRefinementProperty;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.SequenceProperty;
import robocalc.robocert.model.robocert.SequencePropertyType;

/**
 * Generates CSP-M for sequence properties.
 */
public class PropertyLowerer {
	@Inject
	private RoboCertFactory rf;

	/**
	 * Generates CSP-M for a sequence property.
	 *
	 * All sequence properties (thus far) are single direction refinements, where
	 * the LHS and RHS depend on the sequence property operator.
	 *
	 * @param p the property to generate.
	 *
	 * @return the generated property.
	 */
	public CSPRefinementProperty lower(SequenceProperty p) {
		var result = rf.createCSPRefinementProperty();
		result.setLhs(lhs(p));
		result.setRhs(rhs(p));
		// TODO(@MattWindsor91): may change according to GitHub #56
		result.setModel(p.getModel());
		return result;
	}

	/**
	 * Gets the appropriate refinement left-hand side for this sequence property.
	 *
	 * This is always the mirror image of rhs, and is the LHS of a 'refines' (not
	 * 'is-refined-by') relation.
	 *
	 * @param it the property for which we are generating CSP.
	 *
	 * @return the left-hand side process source.
	 */
	private Process lhs(SequenceProperty it) {
		return getSequenceWhenTypeElseTarget(it, SequencePropertyType.IS_OBSERVED);
	}

	/**
	 * Gets the appropriate refinement left-hand side for this sequence property.
	 *
	 * This is always the mirror image of lhs, and is the LHS of a 'refines' (not
	 * 'is-refined-by') relation.
	 *
	 * @param it the property for which we are generating CSP.
	 *
	 * @return the right-hand side process source.
	 */
	private Process rhs(SequenceProperty it) {
		return getSequenceWhenTypeElseTarget(it, SequencePropertyType.HOLDS);
	}

	/**
	 * @param it the sequence property.
	 * @param t  the type that it must have for this call to expand to the sequence.
	 * @return if the sequence property type of it is t, the sequence of t; else,
	 *         the instantiated target of t.
	 */
	private Process getSequenceWhenTypeElseTarget(SequenceProperty it, SequencePropertyType t) {
		return it.getType() == t ? it.getSequence() : target(it);
	}

	/**
	 * @param it the sequence property.
	 * @return a reference to the sequence's fully instantiated target.
	 */
	private Process target(SequenceProperty it) {
		// This can't just be a reference to the original target, as it needs
		// to have the instantiation applied.
		var tgs = rf.createTargetGroupSource();
		tgs.setTargetGroup(it.getSequence().getGroup());
		return tgs;
	}
}