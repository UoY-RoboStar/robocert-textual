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
package robocalc.robocert.generator.utils;

import java.util.stream.Stream;

import javax.inject.Inject;

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.ContextActor;
import robocalc.robocert.model.robocert.Edge;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.SystemModuleActor;
import robocalc.robocert.model.robocert.SystemTarget;

/**
 * Extensions for {@link Edge}s.
 *
 * @author Matt Windsor
 */
public class EdgeExtensions {
	private CSPStructureGenerator csp;
	private CTimedGeneratorUtils gu;
	private TargetExtensions tx;
	
	@Inject
	public EdgeExtensions(CSPStructureGenerator csp, CTimedGeneratorUtils gu, TargetExtensions tx) {
		this.csp = csp;
		this.gu = gu;
		this.tx = tx;
	}
	
	/**
	 * Tries to infer the namespace at a to-actor.
	 *
	 * @param to the to-actor of the .
	 * @return the namespace of the edge.
	 * @throws UnsupportedOperationException if there is no single target.
	 */
	public CharSequence namespace(Actor to) {
		// TODO: asynchronous communications
		
		// The namespacing here follows rule 15 of the RoboChart semantics,
		// in that we usually take the namespace of the 'to' actor.  There is
		// one exception for contexts.
		if (to instanceof ContextActor c)
			return contextNamespace(c);
		if (to instanceof SystemModuleActor s)
			return systemModuleNamespace(s);
		if (to instanceof ComponentActor k)
			return componentNamespace(k);
		throw new UnsupportedOperationException(
					"tried to infer direction of an edge with an ambiguous target");
	}	
	
	private CharSequence contextNamespace(ContextActor c) {
		var tgt = c.getGroup().getTarget();
		// This effectively flips system contexts to take the *from*-actor,
		// as the module must be the only other actor available in a system
		// diagram.
		if (tgt instanceof SystemTarget s)
			return s.getEnclosedModule().getName();
		
		// Business as usual from here on out.
		if (tgt instanceof ModuleTarget m)
			return m.getModule().getName();
		// TODO(@MattWindsor91): controller targets
		throw new IllegalArgumentException("can't get context namespace when actor is %s".formatted(c));
	}
	
	private CharSequence systemModuleNamespace(SystemModuleActor s) {
		if (s.getGroup().getTarget() instanceof SystemTarget t)
			return t.getEnclosedModule().getName();
		throw new IllegalArgumentException("can't get system namespace when actor is %s".formatted(s));
	}
	
	private CharSequence componentNamespace(ComponentActor k) {
		var targetFragments = tx.namePath(k.getGroup().getTarget());
		var componentFragments = Stream.of(gu.connectionNodeName(k.getNode()));
		var fragments = Stream.concat(targetFragments, componentFragments).toArray(String[]::new);
		return csp.namespaced(fragments);		
	}
	
	/**
	 * Infers the direction of this edge.
	 *
	 * @apiNote If the edge is coming from the system module actor, it is
	 *          outbound.  Otherwise, if it is asynchronous, it is inbound.
	 *          Synchronous edges are not yet properly handled, but they
	 *          will be dependent on the context.
	 *
	 * @param edge the edge to query.
	 * @return the direction of the edge.
	 * @throws UnsupportedOperationException if there is no single target.
	 */
	public EdgeDirection getInferredDirection(Edge edge) {
		// TODO(@MattWindsor91): asynchronous edges
		
		if (edge.getResolvedFrom() instanceof SystemModuleActor)
			return EdgeDirection.OUTBOUND;
		return EdgeDirection.INBOUND;
	}
}
