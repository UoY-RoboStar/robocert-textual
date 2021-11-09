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
import robocalc.robocert.model.robocert.MessageDirection;
import robocalc.robocert.model.robocert.SequenceGroup;

/**
 * Inserts actor-seeking functionality into {@link DirectionalActorPairImpl}.
 * 
 * @author Matt Windsor
 */
class DirectionalActorPairImplCustom extends DirectionalActorPairImpl {
	/**
	 * Gets the source of the actor pair by delegating to its 'from' feature.
	 * 
	 * @return the actor pair source.
	 */
	@Override
	public Actor basicGetResolvedFrom() {
		return getWorldOrTargetIf(MessageDirection.OUTBOUND);
	}
	
	/**
	 * Gets the destination of the actor pair by delegating to its 'to' feature.
	 * 
	 * @return the actor pair destination.
	 */
	@Override
	public Actor basicGetResolvedTo() {
		return getWorldOrTargetIf(MessageDirection.INBOUND);
	}
	
	/**
	 * Gets the target of this actor pair's enclosing sequence group if its
	 * direction is equal to the given direction, and the world otherwise.
	 *
	 * @param direction the direction to compare.
	 * @return the actor suggested by the comparison against direction.
	 */
	private Actor getWorldOrTargetIf(MessageDirection direction) {
		var group = EcoreUtil2.getContainerOfType(this, SequenceGroup.class);
		return getDirection() == direction ? group.getTarget() : group.getWorld();
	}
}