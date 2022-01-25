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

/**
 * Adds custom functionality to {@link TargetActorImpl}.
 * 
 * @author Matt Windsor
 */
public class TargetActorImplCustom extends TargetActorImpl {
	@Override
	public String toString() {
		final var name = getName();
		return "<<module>> " + (Strings.isNullOrEmpty(name) ? "(untitled)" : name);
	}
}
