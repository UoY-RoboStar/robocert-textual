/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.utils;

import com.google.inject.Inject;
import java.util.Objects;
import org.eclipse.emf.ecore.util.EcoreUtil;
import robostar.robocert.BinaryMessageSet;
import robostar.robocert.BinarySetOperator;
import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.MessageSet;
import robostar.robocert.UniverseMessageSet;
import robostar.robocert.util.factory.SetFactory;

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
   * <p>
   * Note that the optimisation constructs a new set, and will need to be substituted into the model
   * for containment-sensitive actions to work.
   */
  public MessageSet optimise(MessageSet it) {
    // We can only optimise binary operations, because universe and extensional sets are always
    // optimised, and we don't traverse through references to maintain modularity.
    if (!(it instanceof BinaryMessageSet b)) {
      return it;
    }

    final var lhs = optimise(b.getLhs());
    final var rhs = optimise(b.getRhs());

    return switch (b.getOperator()) {
      case UNION -> optimiseUnion(lhs, rhs);
      case INTERSECTION -> optimiseInter(lhs, rhs);
      case DIFFERENCE -> optimiseDiff(lhs, rhs);
    };
  }

  /**
   * Optimises a union.
   *
   * @param lhs the pre-optimised left-hand side.
   * @param rhs the pre-optimised right-hand side.
   * @return an optimised union between lhs and rhs.
   */
  private MessageSet optimiseUnion(MessageSet lhs, MessageSet rhs) {
    // If both sides are extensional, we can just combine the messages.
    if (lhs instanceof ExtensionalMessageSet el && rhs instanceof ExtensionalMessageSet er) {
      return extensionalUnion(el, er);
    }

    // The universal set saturates a union.
    if (lhs instanceof UniverseMessageSet || rhs instanceof UniverseMessageSet) {
      return setFactory.universe();
    }

    // Drop visibly empty sets.
    if (isVisiblyEmpty(lhs)) {
      return rhs;
    }
    if (isVisiblyEmpty(rhs)) {
      return lhs;
    }

    // Fallback to distribution.
    return setFactory.union(lhs, rhs);
  }

  private ExtensionalMessageSet extensionalUnion(ExtensionalMessageSet el,
      ExtensionalMessageSet er) {
    // Need to copy because, otherwise, we'd accidentally mutate el.
    final var result = EcoreUtil.copy(el);

    // Try to remove duplicates where possible; it's impossible for us to get all of them, as they
    // aren't always structurally equal, but this'll find the ones that are.
    //
    // This is likely to be very slow.
    for (var m : er.getMessages()) {
      if (result.getMessages().stream().anyMatch(x -> EcoreUtil.equals(x, m))) {
        continue;
      }

      // Need to copy because, otherwise, m would be removed from er.
      result.getMessages().add(EcoreUtil.copy(m));
    }

    return result;
  }

  /**
   * Optimises an intersection.
   *
   * @param lhs the pre-optimised left-hand side.
   * @param rhs the pre-optimised right-hand side.
   * @return an optimised intersection between lhs and rhs.
   */
  private MessageSet optimiseInter(MessageSet lhs, MessageSet rhs) {
    // We don't have extensional set optimisation because it's hard to know
    // when two message sets are equivalent.

    // If one side of an intersection is the universe, we can optimise to the other.
    if (lhs instanceof UniverseMessageSet) {
      return rhs;
    }
    if (rhs instanceof UniverseMessageSet) {
      return lhs;
    }

    // If either side is empty, so is an intersection.
    if (isVisiblyEmpty(lhs) || isVisiblyEmpty(rhs)) {
      return setFactory.empty();
    }

    // Fallback to distribution.
    return setFactory.inter(lhs, rhs);
  }

  /**
   * Optimises a difference.
   *
   * @param lhs the left-hand side.
   * @param rhs the right-hand side.
   * @return an optimised difference between lhs and rhs.
   */
  private MessageSet optimiseDiff(MessageSet lhs, MessageSet rhs) {
    // Rewrite ((X \ Y) \ Z) to (X \ (Y u Z)).
    // Why?  Because we can optimise Y u Z more often.
    if (lhs instanceof BinaryMessageSet bl && bl.getOperator() == BinarySetOperator.DIFFERENCE) {
      return optimiseDiff(bl.getLhs(), setFactory.union(bl.getRhs(), rhs));
    }

    // We don't have extensional set optimisation because it's hard to know
    // when two message sets are equivalent.

    // If we're subtracting everything, or have nothing to begin with,
    // we can throw the whole set away.
    if (isVisiblyEmpty(lhs) || rhs instanceof UniverseMessageSet) {
      return setFactory.empty();
    }

    // If we're subtracting nothing, we can optimise the subtraction away.
    if (isVisiblyEmpty(rhs)) {
      return lhs;
    }

    // Fallback to distribution.
    return setFactory.diff(lhs, rhs);
  }

  /**
   * @return whether this set is visibly empty. and we can use that information
   */
  private boolean isVisiblyEmpty(MessageSet it) {
    return it instanceof ExtensionalMessageSet e && e.getMessages().isEmpty();
  }
}
