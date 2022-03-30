/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/
package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.StateMachineRef;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;

/**
 * Resolves RoboChart contexts related to actors.
 *
 * @author Matt Windsor
 */
public record ActorContextFinder(DefinitionResolver defResolver, ActorNodeResolver nodeResolver) {

  // TODO(@MattWindsor91): maybe make this just resolve *nodes* to contexts.

  /**
   * Constructs an actor context finder.
   *
   * @param defResolver  a definition helper used to find robotic platforms in modules.
   * @param nodeResolver used to find the sets of world connection nodes.
   */
  @Inject
  public ActorContextFinder {
    Objects.requireNonNull(defResolver);
  }

  /**
   * Gets the RoboChart contexts in scope of an actor.
   *
   * @param a the actor in question.
   * @return a stream of contexts containing operations, events, and variables visible to the given
   * actor.
   */
  public Stream<Context> contexts(Actor a) {
    return nodeResolver.resolve(a).map(this::contextOfNode);
  }

  /**
   * Retrieves RoboChart contexts deriving from a {@link ComponentActor} attached to the given
   * component.
   *
   * @param n the component for which we are getting contexts.
   * @return the stream of contexts in scope of the actor.
   */
  public Context contextOfNode(ConnectionNode n) {
    // Maybe the node is directly a context?
    if (n instanceof Context x) {
      return x;
    }

    // Resolve references to their definitions, which are contexts.
    if (n instanceof ControllerRef c) {
      return c.getRef();
    }
    if (n instanceof StateMachineRef s) {
      return s.getRef();
    }
    if (n instanceof OperationRef r) {
      return r.getRef();
    }

    throw new IllegalArgumentException("Node not supported for context finding: %s".formatted(n));
  }

}
