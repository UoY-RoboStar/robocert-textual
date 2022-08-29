/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.intf.seq.context;

import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.UntilFragment;

/**
 * Context related to the current lifeline being generated.
 *
 * <p>Depending on where we are in generation, this may be tied to a particular {@link Actor}, or
 * a special linearised context representing an until-fragment.
 */
public interface LifelineContext {

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
   * Gets the index of an until fragment with respect to this lifeline.
   *
   * <p>This differs from the index given by the global context in that, if we are generating
   * inside an until-process, all until-fragments within will be emitted inline and this
   * will return -1.
   *
   * @param frag the fragment whose index is required.
   * @return -1 if this fragment does not have an index; the index of the fragment otherwise.
   */
  int untilIndex(UntilFragment frag);
}
