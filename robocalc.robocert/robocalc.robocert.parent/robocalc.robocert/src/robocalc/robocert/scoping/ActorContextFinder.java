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

import java.util.Optional;
import java.util.stream.Stream;

import com.google.inject.Inject;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.StateMachineRef;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.SystemTarget;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.DefinitionHelper;

/**
 * Resolves RoboChart contexts related to actors.
 *
 * @author Matt Windsor
 */
public record ActorContextFinder(DefinitionHelper dh) {

	/**
	 * Constructs an actor context finder.
	 *
	 * @param dh a definition helper used to find robotic platforms in modules.
	 */
	@Inject
	public ActorContextFinder {
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
		if (a instanceof ComponentActor c) {
			return Optional.of(contextsOfNode(c.getNode()));
		}
		if (a instanceof World x) {
			return Optional.of(contextsOfTargetWorld(x.getGroup().getTarget()));
		}

		// See API note above.
		if (a instanceof TargetActor) {
			return Optional.empty();
		}

		throw new IllegalArgumentException("Actor not supported for context finding: %s".formatted(a));
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
		if (n instanceof Context x) {
			return Stream.of(x);
		}

		// Resolve references to their definitions, which are contexts.
		if (n instanceof ControllerRef c) {
			return Stream.of(c.getRef());
		}
		if (n instanceof StateMachineRef s) {
			return Stream.of(s.getRef());
		}

		throw new IllegalArgumentException("Node not supported for context finding: %s".formatted(n));
	}

	/**
	 * Retrieves RoboChart contexts deriving from a {@link World} attached to the given target.
	 *
	 * @param t the target for which we are getting contexts.
	 * @return the stream of contexts in scope of the actor.
	 */
	private Stream<Context> contextsOfTargetWorld(Target t) {
		// Both of these return the robotic platform of the target's module.
		// Technically, what we are returning for system targets is the
		// contribution of the robotic system's 'world' *via* its platform.
		if (t instanceof SystemTarget s) {
			return contextsOfModuleContext(s.getEnclosedModule());
		}
		// Module target contexts come from the robotic platform directly.
		if (t instanceof ModuleTarget m) {
			return contextsOfModuleContext(m.getModule());
		}

		// TODO(@MattWindsor91): controller target contexts should other controllers as
		// well as the module.

		// Not yet supported.
		throw new IllegalArgumentException("Target not supported for context finding: %s".formatted(t));
	}

	private Stream<Context> contextsOfModuleContext(RCModule m) {
		return dh.platform(m).map((Context c) -> c).stream();
	}
}
