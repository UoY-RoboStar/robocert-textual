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

import robocalc.robocert.model.robocert.Edge;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.TargetActor;

/**
 * Extensions for {@link Edge}s.
 *
 * @author Matt Windsor
 */
public class EdgeExtensions {
	/**
	 * Tries to infer the direction of this edge.
	 *
	 * An edge can only have a direction if exactly one of its actors is a
	 * target actor. If so, then the direction is outbound if, and only if, that
	 * target actor is the from-actor.
	 *
	 * @param edge the edge to query.
	 * @return the direction of the edge.
	 * @throws UnsupportedOperationException if there is no single target.
	 */
	public EdgeDirection getInferredDirection(Edge edge) {
		if (edge.getResolvedFrom() instanceof TargetActor)
			return EdgeDirection.OUTBOUND;
		if (edge.getResolvedTo() instanceof TargetActor)
			return EdgeDirection.INBOUND;
		throw new UnsupportedOperationException(
					"tried to infer direction of an edge with an ambiguous target");
	}
}
