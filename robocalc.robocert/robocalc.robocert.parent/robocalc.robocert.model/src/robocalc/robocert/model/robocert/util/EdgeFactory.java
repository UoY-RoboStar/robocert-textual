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
package robocalc.robocert.model.robocert.util;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.Edge;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.ExplicitEdge;
import robocalc.robocert.model.robocert.ImplicitEdge;
import robocalc.robocert.model.robocert.RoboCertFactory;

/**
 * High-level factory for {@link Edge}s.
 *
 * @author Matt Windsor
 */
public record EdgeFactory(RoboCertFactory rf) {

	/**
	 * Constructs an edge factory.
	 *
	 * @param rf the underlying metamodel factory.
	 */
	@Inject
	public EdgeFactory {
	}

	/**
	 * Constructs an implicit edge.
	 *
	 * @param d the direction of the edge.
	 * @return the edge.
	 */
	public ImplicitEdge edge(EdgeDirection d) {
		final var it = rf.createImplicitEdge();
		it.setDirection(d);
		return it;
	}

	/**
	 * Constructs an explicit edge.
	 *
	 * @param from the from-actor of the edge.
	 * @param to   the to-actor of the edge.
	 * @return the edge.
	 */
	public ExplicitEdge edge(Actor from, Actor to) {
		final var it = rf.createExplicitEdge();
		it.setFrom(from);
		it.setTo(to);
		return it;
	}

	/**
	 * Constructs an explicit edge whose from- and to-actors are the resolved actors from the incoming
	 * edge.
	 *
	 * @param e the edge to resolve.
	 * @return the resolved, explicit, edge.
	 */
	public ExplicitEdge resolvedEdge(Edge e) {
		return edge(e.getResolvedFrom(), e.getResolvedTo());
	}
}
