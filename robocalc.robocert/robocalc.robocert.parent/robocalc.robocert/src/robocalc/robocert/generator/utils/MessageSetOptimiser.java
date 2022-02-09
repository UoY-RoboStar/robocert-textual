/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.utils;

import com.google.inject.Inject;
import java.util.Objects;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.model.robocert.BinaryMessageSet;
import robocalc.robocert.model.robocert.BinarySetOperator;
import robocalc.robocert.model.robocert.ExtensionalMessageSet;
import robocalc.robocert.model.robocert.MessageSet;
import robocalc.robocert.model.robocert.RefMessageSet;
import robocalc.robocert.model.robocert.util.SetFactory;

/**
 * Functionality for optimising message sets.
 *
 * @author Matt Windsor
 */
public record MessageSetOptimiser(SetFactory setFactory) {
	@Inject
	public MessageSetOptimiser {
		Objects.requireNonNull(setFactory);
	}

	/**
	 * Optimises a message set.
	 * 
	 * Note that the optimisation constructs a new set, and will need to be
	 * substituted into the model for containment-sensitive actions to work.
	 */
	public MessageSet optimise(MessageSet it) {
		if (isVisiblyInactive(it)) {
			return setFactory.empty();
		}
		if (isVisiblyUniversal(it)) {
			return setFactory.universe();
		}
		if (it instanceof BinaryMessageSet b) {
			return optimiseBinary(b);
		}
		// We can't optimise this message set.
		return it;
	}

	private MessageSet optimiseBinary(BinaryMessageSet it) {
		return switch (it.getOperator()) {
			case UNION ->
				optimiseUnion(it.getLhs(), it.getRhs());
			case INTERSECTION ->
				optimiseInter(it.getLhs(), it.getRhs());
			case DIFFERENCE ->
				optimiseDiff(it.getLhs(), it.getRhs());
		};
	}

	/**
	 * Optimises a union.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised union between lhs and rhs.
	 */	
	private MessageSet optimiseUnion(MessageSet lhs, MessageSet rhs) {
		// If both sides are extensional, we can just combine the messages.
		if (lhs instanceof ExtensionalMessageSet el && rhs instanceof ExtensionalMessageSet er) {
			final var result = EcoreUtil2.copy(el);
			result.getMessages().addAll(er.getMessages());
			return result;
		}

		// The universal set saturates a union.
		if (isVisiblyUniversal(lhs) || isVisiblyUniversal(rhs)) {
			return setFactory.universe();
		}

		// Drop visibly inactive sets.
		if (isVisiblyInactive(lhs)) {
			return optimise(rhs);
		}
		if (isVisiblyInactive(rhs)) {
			return optimise(lhs);
		}

		// Fallback to distribution.
		return setFactory.union(optimise(lhs), optimise(rhs));
	}

	/**
	 * Optimises an intersection.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised intersection between lhs and rhs.
	 */
	private MessageSet optimiseInter(MessageSet lhs, MessageSet rhs) {
		// We don't have extensional set optimisation because it's hard to know
		// when two message sets are equivalent.
		if (isVisiblyUniversal(lhs) && isVisiblyUniversal(rhs)) {
			return setFactory.universe();
		}
		if (isVisiblyInactive(lhs) || isVisiblyInactive(rhs)) {
			return setFactory.empty();
		}

		// Fallback to distribution.
		return setFactory.inter(optimise(lhs), optimise(rhs));
	}

	/**
	 * Optimises a difference.
	 * 
	 * @param lhs  the left-hand side.
	 * @param rhs  the right-hand side.
	 * @return an optimised difference between lhs and rhs.
	 */	
	private  MessageSet optimiseDiff(MessageSet lhs, MessageSet rhs) {
		// Rewrite ((X \ Y) \ Z) to (X \ (Y u Z)).
		// Why?  Because we can optimise Y u Z more often.
		if (lhs instanceof BinaryMessageSet bl && bl.getOperator() == BinarySetOperator.DIFFERENCE) {
			return optimiseDiff(bl.getLhs(), setFactory.union(bl.getRhs(), rhs));
		}

		// We don't have extensional set optimisation because it's hard to know
		// when two message sets are equivalent.
		if (isVisiblyInactive(lhs) || isVisiblyUniversal(rhs)) {
			// If we're subtracting everything, or have nothing to begin with,
			// we can throw the whole set away.
			return setFactory.empty();
		}

		// Fallback to distribution.
		return setFactory.diff(optimise(lhs), optimise(rhs));
	}

	/**
	 * @return whether this set is universal and we can use that information
	 *         to optimise.
	 */
	private boolean isVisiblyInactive(MessageSet it) {
		return !it.isActive() && isTransparent(it);
	}
	
	/**
	 * @return whether this set is inactive and we can use that information
	 *         to optimise.
	 */
	private boolean isVisiblyUniversal(MessageSet it) {
		return it.isUniversal() && isTransparent(it);
	}
	
	/**
	 * Is the nature of this message set known without traversing references?
	 * 
	 * We don't optimise past message set references, because that means that
	 * any changes to the message set invalidate the equivalence of the
	 * optimisation.
	 * 
	 * @return whether the message set can be optimised away.
	 */
	private boolean isTransparent(MessageSet it) {
		return !(it instanceof RefMessageSet);
	}
}