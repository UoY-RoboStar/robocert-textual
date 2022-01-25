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
		return getContextOrModuleIf(EdgeDirection.OUTBOUND);
	}
	
	/**
	 * Gets the destination of the edge by delegating to its 'to' feature.
	 * 
	 * @return the edge destination.
	 */
	@Override
	public Actor basicGetResolvedTo() {
		return getContextOrModuleIf(EdgeDirection.INBOUND);
	}
	
	/**
	 * Gets the system module target lifeline of this edge's enclosing sequence
	 * if its direction is equal to the given direction, and the context
	 * otherwise.
	 *
	 * @param direction the direction to compare.
	 * @return the actor suggested by the comparison against direction.
	 */
	private Actor getContextOrModuleIf(EdgeDirection direction) {
		// NOTE(@MattWindsor91): this has to work even if this edge is in a
		// message set eg. we can't check for sequence features here.
		var grp = EcoreUtil2.getContainerOfType(this, SequenceGroup.class);
		return getDirection() == direction ? grp.getTargetActor() : grp.getWorld();
	}
}