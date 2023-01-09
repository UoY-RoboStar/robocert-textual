/*
 * Copyright (c) 2022, 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.intf.seq.context;

import circus.robocalc.robochart.Variable;
import java.util.function.Function;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.SequentialFragment;

/**
 * Context related to the current lifeline being generated.
 *
 * <p>Depending on where we are in generation, this may be tied to a particular {@link Actor}, or
 * a sequential process used for realising sequential fragments.
 */
public interface LifelineContext {

  /**
   * Gets the variables in scope for this lifeline.
   *
   * @return a stream of the lifeline's in-scope variables.
   */
  Stream<Variable> variables();

  /**
   * Does this lifeline require a memory?
   *
   * @return true if the lifeline requires a memory (generally, there is at least one lifeline-local
   * variable in scope for this lifeline).
   */
  default boolean needsMemory() {
    return variables().findAny().isPresent();
  }

  /**
   * Is this context for the given actor?
   *
   * @param a the actor to check against.
   * @return true provided that this context is building the lifeline for {@code a}.
   */
  boolean isFor(Actor a);

  /**
   * Is this context for any of the actors in the given stream?
   *
   * @param actors the actors to check against.
   * @return true provided that this context is building the lifeline for one of {@code actors}.
   */
  default boolean isForAnyOf(Stream<Actor> actors) {
    return actors.anyMatch(this::isFor);
  }

  /**
   * Gets the name of the actor, if any, to which this context belongs.
   *
   * @return the name (human-readable).
   */
  String actorName();

  /**
   * Gets the parent interaction context of this lifeline context.
   *
   * @return the interaction context.
   */
  InteractionContext global();

  /**
   * Handles a sequential fragment according to whether this lifeline is in sequential position or
   * not.
   *
   * @param f             fragment to be expanded.
   * @param <T>           type of output for this sequential fragment.
   * @param emitInline    function to be used if we are in sequential position (and want the
   *                      fragment expanded).
   * @param emitReference function to be used if we are not in sequential position (accepts the
   *                      fragment index).
   * @return the result of whichever of the emitting functions was called.
   */
  <T> T handleSequential(SequentialFragment f, Function<SequentialFragment, T> emitInline,
      Function<Integer, T> emitReference);
}
