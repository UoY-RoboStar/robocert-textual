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

import java.util.stream.Stream;

import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.Target;

/**
 * Extension methods for dealing with targets.
 *
 * @author Matt Windsor
 */
public class TargetHelper {
	// TODO(@MattWindsor91): move to metamodel?
	
	public Stream<String> namePath(Target t) {
		if (t instanceof ModuleTarget m)
			return Stream.of(m.getModule().getName());
		
		// TODO(@MattWindsor91): controller targets
		
		throw new IllegalArgumentException("can't get name path of target: %s".formatted(t));
	}
}
