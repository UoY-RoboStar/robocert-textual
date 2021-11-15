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

import com.google.common.base.Strings;

import robocalc.robocert.model.robocert.TargetActorRelationship;

/**
 * Adds derived operation definitions to {@link StandardActorImplCustom}.
 * 
 * @author Matt Windsor
 */
class StandardActorImplCustom extends StandardActorImpl {
	@Override
	public TargetActorRelationship getCalculatedRelationship() {
		// No need to calculate one here - standard actors are defined
		// precisely by their relationship to the target.
		return relationship;
	}
	
	/**
	 * @return a human-friendly representation of this actor.
	 */
	@Override
	public String toString() {
		return "<<%s>> %s".formatted(relationshipString(), nameOrUnknown());
	}
	
	private String relationshipString() {
		return switch (getRelationship()) {
		case WORLD -> "world";
		case TARGET -> "target";
		default -> "unknown";
		};
	}
	
	private String nameOrUnknown() {
		var n = getName();
		return Strings.isNullOrEmpty(n) ? "(untitled)" : n;
	}
}