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
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentTarget;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.World;

/**
 * Creates, from a sequence, a series of lifeline contexts for use in generating individual
 * lifelines.
 *
 * @param actorGenerator used to get data constructor names for actors.
 * @author Matt Windsor
 */
public record LifelineContextFactory(ActorGenerator actorGenerator) {

  /**
   * Constructs a lifeline context factory.
   *
   * @param actorGenerator used to get data constructor names for actors.
   */
  @Inject
  public LifelineContextFactory {
    Objects.requireNonNull(actorGenerator);
  }

  /**
   * Creates contexts for each semantics-visible lifeline in the given sequence.
   *
   * <p>Not all lifelines are visible; any that form a context do not appear in the semantics as
   * they are considered to be the CSP environment.
   *
   * @param s the sequence for which we are creating contexts.
   * @return the list of contexts.
   */
  public List<LifelineContext> createContexts(Interaction s) {
    final var target = s.getGroup().getTarget();

    final var visibleActors = s.getActors().stream().filter(this::actorVisibleInSemantics).toList();
    final var isSingleton = target instanceof ComponentTarget || visibleActors.size() < 2;

    return visibleActors.parallelStream()
        .map(a -> new LifelineContext(a, actorGenerator.dataConstructor(a), isSingleton)).toList();
  }

  private boolean actorVisibleInSemantics(Actor a) {
    // This may change in future.
    return !(a instanceof World);
  }
}
