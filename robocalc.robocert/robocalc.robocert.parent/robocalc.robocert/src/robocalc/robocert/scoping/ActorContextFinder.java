/********************************************************************************
 * Copyright (c) 2021 University of York and others
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
package robocalc.robocert.scoping;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.StateMachineRef;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.util.ActorNodeResolver;
import robocalc.robocert.model.robocert.util.DefinitionResolver;

/**
 * Resolves RoboChart contexts related to actors.
 *
 * @author Matt Windsor
 */
public record ActorContextFinder(DefinitionResolver defResolver, ActorNodeResolver nodeResolver) {

	/**
	 * Constructs an actor context finder.
	 *
	 * @param defResolver a definition helper used to find robotic platforms in modules.
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
	 * @apiNote An empty-optional result (not an empty stream) denotes the special case of a system
	 * module actor. These do not report any contexts: any sequence diagram using a system module
	 * actor only has communications between it and a context actor, and we can get the contexts
	 * unambiguously from just the latter.
	 */
	public Optional<Stream<Context>> contexts(Actor a) {
		return Optional.of(nodeResolver.resolve(a).flatMap(this::contextsOfNode));
	}

	/**
	 * Retrieves RoboChart contexts deriving from a {@link ComponentActor} attached to the given
	 * component.
	 *
	 * @param n the component for which we are getting contexts.
	 * @return the stream of contexts in scope of the actor.
	 */
	private Stream<Context> contextsOfNode(ConnectionNode n) {
		// Maybe the node is directly a context?
		if (n instanceof Context x)
			return Stream.of(x);

		// Resolve references to their definitions, which are contexts.
		if (n instanceof ControllerRef c)
			return Stream.of(c.getRef());
		if (n instanceof StateMachineRef s)
			return Stream.of(s.getRef());
		if (n instanceof OperationRef r)
			return Stream.of(r.getRef());

		throw new IllegalArgumentException("Node not supported for context finding: %s".formatted(n));
	}

}
