package robocalc.robocert.generator.utils

import robocalc.robocert.model.robocert.MessageSet
import robocalc.robocert.model.robocert.util.SetFactory
import robocalc.robocert.model.robocert.RefMessageSet
import robocalc.robocert.model.robocert.BinaryMessageSet
import robocalc.robocert.model.robocert.ExtensionalMessageSet
import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.model.robocert.BinarySetOperator
import com.google.inject.Inject

/**
 * Functionality for optimising message sets.
 */
class MessageSetOptimiser {
	@Inject extension SetFactory

	/**
	 * Optimises a message set.
	 * 
	 * Note that the optimisation constructs a new set, and will need to be
	 * substituted into the model for containment-sensitive actions to work.
	 */
	def MessageSet optimise(MessageSet it) {
		if (visiblyInactive) {
			empty
		} else if (visiblyUniversal) {
			universe
		} else {
			optimiseInner
		}
	}
	
	def private dispatch optimiseInner(BinaryMessageSet it) {
		switch operator {
			case UNION:
				optimiseUnion(lhs, rhs)
			case INTERSECTION:
				optimiseInter(lhs, rhs)
			case DIFFERENCE:
				optimiseDiff(lhs, rhs)
			default:
				it
		}
	}

	/**
	 * Optimises an extensional union into an extensional set.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised union between lhs and rhs.
	 */	
	def private dispatch optimiseUnion(ExtensionalMessageSet lhs, ExtensionalMessageSet rhs) {
		EcoreUtil2.copy(lhs) => [
			messages.addAll(rhs.messages)
		]
	}
	
	/**
	 * Generic distributing optimisation on union between two sets.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised union between lhs and rhs.
	 */
	def private dispatch optimiseUnion(MessageSet lhs, MessageSet rhs) {
		// The universal set saturates a union.
		if (lhs.visiblyUniversal || rhs.visiblyUniversal) {
			universe
		} else if (lhs.visiblyInactive) {
			optimise(rhs)
		} else if (rhs.visiblyInactive) {
			optimise(lhs)
		} else {
			union(optimise(lhs), optimise(rhs))
		}
	}

	/**
	 * Distributing optimisation on intersection between two sets.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised intersection between lhs and rhs.
	 */
	def private optimiseInter(MessageSet lhs, MessageSet rhs) {
		// We don't have extensional set optimisation because it's hard to know
		// when two message sets are equivalent.
		if (lhs.visiblyUniversal && rhs.visiblyUniversal) {
			universe
		} else if (lhs.visiblyInactive || rhs.visiblyInactive) {
			empty
		} else {
			inter(optimise(lhs), optimise(rhs))
		}
	}

	/**
	 * Special-case for differences where the LHS might itself be a difference.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised difference between lhs and rhs.
	 */	
	def private dispatch optimiseDiff(BinaryMessageSet lhs, MessageSet rhs) {
		// Rewrite ((X \ Y) \ Z) to (X \ (Y u Z)).
		// Why?  Because we can optimise Y u Z more often.
		if (lhs.operator == BinarySetOperator::DIFFERENCE) {
			optimiseDiffFallback(lhs.lhs, union(lhs.rhs, rhs))
		} else {
			optimiseDiffFallback(lhs, rhs)
		}
	}

	/**
	 * Generic distributing optimisation on difference between two sets.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised difference between lhs and rhs.
	 */
	def private dispatch optimiseDiff(MessageSet lhs, MessageSet rhs) {
		optimiseDiffFallback(lhs, rhs)
	}
	
	def private optimiseDiffFallback(MessageSet lhs, MessageSet rhs) {
		// We don't have extensional set optimisation because it's hard to know
		// when two message sets are equivalent.
		if (lhs.visiblyInactive || rhs.universal) {
			// If we're subtracting everything, or have nothing to begin with,
			// we can throw the whole set away.
			empty
		} else {
			diff(optimise(lhs), optimise(rhs))
		}
	}

	
	/**
	 * @return the set unmodified, as we can't optimise it anymore.
	 */
	def private dispatch optimiseInner(MessageSet it) {
		it
	}
	
	/**
	 * @return whether this set is universal and we can use that information
	 *         to optimise.
	 */
	def private isVisiblyInactive(MessageSet it) {
		!active && !opaque
	}
	
	/**
	 * @return whether this set is inactive and we can use that information
	 *         to optimise.
	 */
	def private isVisiblyUniversal(MessageSet it) {
		universal && !opaque
	}
	
	/**
	 * Checks for opaqueness.
	 * 
	 * We don't optimise past message set references, because that means that
	 * any changes to the message set invalidate the equivalence of the
	 * optimisation.
	 * 
	 * @return whether the message set should not be optimised away.
	 */
	def private isOpaque(MessageSet it) {
		it instanceof RefMessageSet
	}
}