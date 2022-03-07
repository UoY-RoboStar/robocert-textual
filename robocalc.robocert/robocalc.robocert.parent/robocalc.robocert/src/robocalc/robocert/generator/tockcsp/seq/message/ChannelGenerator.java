/*******************************************************************************
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
 ******************************************************************************/
package robocalc.robocert.generator.tockcsp.seq.message;

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import java.util.stream.Stream;
import javax.inject.Inject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.utils.TargetHelper;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.World;

/**
 * Generates CSP-M for message channels.
 * <p>
 * This part of the generator does not take into account arguments, but it has to take into account
 * both the topic and the edge of the message.
 *
 * @author Matt Windsor
 */
public record ChannelGenerator(CSPStructureGenerator csp,
															 CTimedGeneratorUtils gu,
															 TargetHelper tx) {

	/**
	 * Constructs a channel generator.
	 *
	 * @param csp CSP structure generator, used mainly for constructing namespaced references.
	 * @param gu  RoboChart generator utilities.
	 * @param tx  Helpers for working with targets.
	 */
	@Inject
	public ChannelGenerator {
	}

	/**
	 * Tries to infer the namespace at a to-actor.
	 *
	 * @param to the to-actor of the edge.
	 * @return the namespace of the edge.
	 * @throws UnsupportedOperationException if there is no single target.
	 */
	public CharSequence namespace(Actor to) {
		// TODO(@MattWindsor91): this needs radical rethinking for multi-actor
		// sequences.
		
		// TODO: asynchronous communications

		// The namespacing here follows rule 15 of the RoboChart semantics,
		// in that we usually take the namespace of the 'to' actor.  There is
		// one exception for worlds.
		if (to instanceof World c) {
			return worldNamespace(c);
		}
		if (to instanceof TargetActor s) {
			return targetNamespace(s);
		}
		if (to instanceof ComponentActor k) {
			return componentNamespace(k);
		}
		throw new UnsupportedOperationException(
				"tried to infer direction of an edge with an ambiguous target");
	}

	private CharSequence worldNamespace(World c) {
		final var tgt = getTarget(c);
		// This effectively flips system contexts to take the *from*-actor,
		// as the module must be the only other actor available in a system
		// diagram.
		if (tgt instanceof ModuleTarget m) {
			return m.getModule().getName();
		}
		// TODO(@MattWindsor91): controller targets
		throw new IllegalArgumentException("can't get context namespace when actor is %s".formatted(c));
	}

	private CharSequence targetNamespace(TargetActor s) {
		if (getTarget(s) instanceof ModuleTarget t) {
			return t.getModule().getName();
		}
		throw new IllegalArgumentException("can't get target namespace when actor is %s".formatted(s));
	}

	private CharSequence componentNamespace(ComponentActor k) {
		final var targetFragments = tx.namePath(getTarget(k));
		final var componentFragments = Stream.of(gu.connectionNodeName(k.getNode()));
		final var fragments = Stream.concat(targetFragments, componentFragments).toArray(String[]::new);
		return csp.namespaced(fragments);
	}

	/**
	 * Infers the channel direction of this message.
	 *
	 * @param msg the message to query.
	 * @return the direction of the edge.
	 * @throws UnsupportedOperationException if there is no single target.
	 * @apiNote If the edge is coming from the target module actor, it is "out".  Otherwise, if it
	 * is synchronous, it is "in". Asynchronous edges are not yet properly handled, but they will
	 * be dependent on the context.
	 */
	public String inferDirection(Message msg) {
		// TODO(@MattWindsor91): asynchronous edges

		if (msg.getFrom() instanceof TargetActor) {
			return "out";
		}
		return "in";
	}

	private Target getTarget(Actor a) {
		return a.getGroup().getTarget();
	}
}
