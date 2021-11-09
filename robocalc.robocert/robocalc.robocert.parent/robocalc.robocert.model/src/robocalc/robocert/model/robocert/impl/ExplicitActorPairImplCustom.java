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

import robocalc.robocert.model.robocert.Actor;

/**
 * Inserts actor-seeking functionality into {@link ExplicitActorPairImpl}.
 * 
 * @author Matt Windsor
 */
class ExplicitActorPairImplCustom extends ExplicitActorPairImpl {
	/**
	 * Gets the source of the actor pair by delegating to its 'from' feature.
	 * 
	 * @return the actor pair source.
	 */
	@Override
	public Actor basicGetResolvedFrom() {
		return getFrom();
	}
	
	/**
	 * Gets the destination of the actor pair by delegating to its 'to' feature.
	 * 
	 * @return the actor pair destination.
	 */
	@Override
	public Actor basicGetResolvedTo() {
		return getTo();
	}
}