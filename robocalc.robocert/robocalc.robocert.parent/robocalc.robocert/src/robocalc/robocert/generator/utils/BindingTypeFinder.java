/*******************************************************************************
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
 ******************************************************************************/

package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.Type;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.WildcardValueSpecification;

/**
 * Finds the RoboChart type of bindings by traversing the object graph back to where they were
 * used.
 *
 * @author Matt Windsor
 */
public class BindingTypeFinder {
	// TODO(@MattWindsor91): expose this through the metamodel.

	/**
	 * Gets the type of a {@link Binding} by traversing its container(s).
	 * <p>
	 * For instance, a binding within a {@link Message} will be resolved against the parameters of
	 * its corresponding {@link MessageTopic}.
	 *
	 * @param b the binding whose type is to be resolved.
	 * @return the RoboChart type of the binding.
	 */
	public Type getType(Binding b) {
		final var parent = b.eContainer();

		if (parent instanceof WildcardValueSpecification arg) {
			return typeFromWildcardArgument(arg);
		}

		// Add other locations for bindings here.

		throw new UnsupportedOperationException("Unsupported binding container: " + parent);
	}

	private Type typeFromWildcardArgument(WildcardValueSpecification arg) {
		final var parent = arg.eContainer();

		if (parent instanceof Message spec) {
			return typeFromMessage(spec, arg);
		}

		// If WildcardValueSpecifications can ever come from things other than message
		// specs, add code for them here.

		throw new UnsupportedOperationException("Unsupported wildcard value specification container: " + parent);
	}

	private Type typeFromMessage(Message spec, WildcardValueSpecification arg) {
		final var i = spec.getArguments().indexOf(arg);
		return spec.getTopic().getParamTypes().get(i);
	}
}
