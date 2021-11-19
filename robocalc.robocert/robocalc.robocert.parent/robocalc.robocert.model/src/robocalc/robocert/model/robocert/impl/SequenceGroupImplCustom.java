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

import com.google.common.collect.Iterables;

import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.WorldActor;

/**
 * Adds derived operation definitions to {@link SequenceGroupImpl}.
 *
 * @author Matt Windsor
 */
public class SequenceGroupImplCustom extends SequenceGroupImpl {
	@Override
	public WorldActor basicGetWorldActor() {
		return Iterables.getFirst(Iterables.filter(getActors(), WorldActor.class), null);
	}

	@Override
	public TargetActor basicGetTargetActor() {
		return Iterables.getFirst(Iterables.filter(getActors(), TargetActor.class), null);
	}
}