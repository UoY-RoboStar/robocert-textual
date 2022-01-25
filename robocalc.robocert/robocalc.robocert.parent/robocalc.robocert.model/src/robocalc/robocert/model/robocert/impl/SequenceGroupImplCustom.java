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
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * Adds derived operation definitions to {@link SequenceGroupImpl}.
 *
 * @author Matt Windsor
 */
public class SequenceGroupImplCustom extends SequenceGroupImpl {
	@Override
	public TargetActor basicGetTargetActor() {
		return getFirstActor(TargetActor.class);
	}

	@Override
	public World basicGetWorld() {
		return getFirstActor(World.class);
	}

	private <T extends Actor> T getFirstActor(Class<T> clazz) {
		return StreamHelpers.filter(getActors().parallelStream(), clazz).findFirst().orElse(null);
	}
}