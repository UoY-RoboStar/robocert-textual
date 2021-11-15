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
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.SequenceGroup;

/**
 * Inserts actor-seeking functionality into {@link ImplicitActorPairImpl}.
 * 
 * @author Matt Windsor
 */
class ImplicitActorPairImplCustom extends ImplicitActorPairImpl {
	/**
	 * Gets the source of the actor pair.
	 * 
	 * The exact calculation here depends on the topic.
	 * 
	 * @return the actor pair source.
	 */
	@Override
	public Actor basicGetResolvedFrom() {
		return getResolvedActor(false);
	}
	
	/**
	 * Gets the destination of the actor pair.
	 * 
	 * @return the actor pair destination.
	 */
	@Override
	public Actor basicGetResolvedTo() {
		return getResolvedActor(true);
	}
	
	private Actor getResolvedActor(boolean isTo) {
		var topic = getSpec().getTopic();
		if (topic instanceof OperationTopic)
			return getOperationActor(isTo);
		throw new UnsupportedOperationException("can't calculate implicit actors for topic %s".formatted(topic));
	}

	/**
	 * Gets one of the implicit actors in a pair on an operation message.
	 * 
	 * An operation message is always from a target to a world, which
	 * means that the returned actor will be the enclosing group's target
	 * if isTo is false, and the world if isTo is true.
	 * 
	 * @param isTo whether we are asking for the 'to' edge of the pair.
	 * @return the required actor of the pair.
	 */
	private Actor getOperationActor(boolean isTo) {
		// TODO(@MattWindsor91): this is very similar to DirectionalActorPairImplCustom;
		// it'd be nice to have the common code factored out?
		var grp = EcoreUtil2.getContainerOfType(this, SequenceGroup.class);
		return isTo ? grp.getWorldActor() : grp.getTargetActor();
	}
}