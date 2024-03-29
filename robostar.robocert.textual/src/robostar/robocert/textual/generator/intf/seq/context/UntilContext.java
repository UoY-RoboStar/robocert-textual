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

import java.util.Objects;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.UntilFragment;

/**
 * A lifeline context that represents the situation while generating the body of an until-process.
 *
 * In such processes, there is no separation between lifelines, whose actions are fully linearised.
 * This means that any queries to check whether an actor is relevant come back true, and any
 * attempts to expand another until fragment inside the body do so directly instead of referencing
 * the until process.
 *
 * @param global the global interaction context.
 */
public record UntilContext(InteractionContext global) implements LifelineContext {

  /**
   * Constructs an until context.
   * @param global the global interaction context.
   */
  public UntilContext {
    Objects.requireNonNull(global);
  }

  @Override
  public boolean isFor(Actor a) {
    // UntilContexts represent all lifelines simultaneously, so they are match all actors.
    return true;
  }

  @Override
  public boolean isForAnyOf(Stream<Actor> actors) {
    // As above.
    return true;
  }

  @Override
  public String actorName() {
    return "[UNTIL]";
  }

  @Override
  public int untilIndex(UntilFragment frag) {
    // Any fragments encountered within an until-process generation process should be inlined,
    // not synchronised with the same process we're generating!
    return -1;
  }
}
