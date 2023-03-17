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

import java.util.List;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;

import java.util.stream.Stream;
import robostar.robocert.util.resolve.result.ResolvedVariable;

/**
 * Common-denominator interface for interaction contexts.
 *
 * @author Matt Windsor
 */
public interface InteractionContext {

  /**
   * Gets the interaction for which this is a context.
   *
   * @return the interaction.
   */
  Interaction interaction();

  /**
   * Gets the list of all actors in this context's interaction.
   *
   * @return the list of all actors
   */
  default List<Actor> allActors() {
    return interaction().getGroup().getActors();
  }


  /**
   * Gets contexts for each of the lifelines in this interaction.
   *
   * @return a list of lifeline contexts.
   */
  Stream<LifelineContext> lifelines();

  /**
   * Gets the number of lifelines in this interaction.
   *
   * @return the number of lifelines (equal to the size of {@code lifelines()}).
   */
  default long numLifelines() {
    return lifelines().count();
  }

  /**
   * Gets all the variables reachable from this interaction.
   *
   * @return a list of resolved variables.
   */
  Stream<ResolvedVariable> variables();

  /**
   * Does this interaction need memory processes?
   *
   * @return true if, and only if, at least one specification variable exists in the interaction.
   */
  default boolean needsMemory() {
    return variables().findAny().isPresent();
  }
}
