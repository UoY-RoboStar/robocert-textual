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

import circus.robocalc.robochart.Variable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import robostar.robocert.Actor;
import robostar.robocert.SequentialFragment;

/**
 * A lifeline context that represents the situation while generating the body of a sequential
 * process.
 *
 * <p>
 * In such processes, there is no separation between lifelines, whose actions are fully linearised.
 * This means that any queries to check whether an actor is relevant come back true, and any
 * attempts to expand another until fragment inside the body do so directly instead of referencing
 * the until process.
 *
 * @param global the global interaction context.
 */
public record SequentialContext(InteractionContext global) implements LifelineContext {

  /**
   * Constructs a sequential context.
   *
   * @param global the global interaction context.
   */
  public SequentialContext {
    Objects.requireNonNull(global);
  }

  @Override
  public Stream<Variable> variables() {
    // Sequential contexts currently have no access to any variables.
    return Stream.empty();
  }

  @Override
  public boolean needsMemory() {
    // Sequential contexts never generate a memory.
    return false;
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
    return "[SEQUENTIAL]";
  }

  @Override
  public <T> T handleSequential(SequentialFragment f, Function<SequentialFragment, T> emitInline,
      Function<Integer, T> emitReference) {
    return emitInline.apply(f);
  }
}