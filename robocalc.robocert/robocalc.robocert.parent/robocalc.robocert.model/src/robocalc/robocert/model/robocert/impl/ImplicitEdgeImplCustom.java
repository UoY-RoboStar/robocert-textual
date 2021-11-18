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
package robocalc.robocert.model.robocert.impl;

import org.eclipse.xtext.EcoreUtil2;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.SequenceGroup;

/**
 * Inserts actor-seeking functionality into {@link ImplicitEdgeImpl}.
 * 
 * @author Matt Windsor
 */
class ImplicitEdgeImplCustom extends ImplicitEdgeImpl {
	/**
	 * Gets the source of the edge by delegating to its 'from' feature.
	 * 
	 * @return the edge source.
	 */
	@Override
	public Actor basicGetResolvedFrom() {
		return getWorldOrTargetIf(EdgeDirection.OUTBOUND);
	}
	
	/**
	 * Gets the destination of the edge by delegating to its 'to' feature.
	 * 
	 * @return the edge destination.
	 */
	@Override
	public Actor basicGetResolvedTo() {
		return getWorldOrTargetIf(EdgeDirection.INBOUND);
	}
	
	/**
	 * Gets the target of this edge's enclosing sequence group if its
	 * direction is equal to the given direction, and the world otherwise.
	 *
	 * @param direction the direction to compare.
	 * @return the actor suggested by the comparison against direction.
	 */
	private Actor getWorldOrTargetIf(EdgeDirection direction) {
		var grp = EcoreUtil2.getContainerOfType(this, SequenceGroup.class);
		return getDirection() == direction ? grp.getTargetActor() : grp.getWorldActor();
	}
}