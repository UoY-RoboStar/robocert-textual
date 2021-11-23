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

import com.google.inject.Inject;

import circus.robocalc.robochart.Context;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.SystemTarget;
import robocalc.robocert.model.robocert.Target;

/**
 * Extension methods for dealing with targets.
 *
 * @author Matt Windsor
 */
public class TargetExtensions {
	// TODO(@MattWindsor91): move some of these to the metamodel?
	@Inject
	private RCModuleExtensions mx;

	/**
	 * Gets the contexts that are in scope at a target.
	 *
	 * @param t the module target in question.
	 *
	 * @return the world, as an iterator over contexts.
	 */
	public Stream<Context> contexts(Target t) {
		if (t instanceof SystemTarget s)
			return mx.contexts(s.getEnclosedModule());
		if (t instanceof ModuleTarget m)
			return mx.contexts(m.getModule());
		throw new IllegalArgumentException("don't know how to get the contexts of target %s".formatted(t));
	}
}
