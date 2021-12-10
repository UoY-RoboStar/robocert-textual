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

import com.google.inject.Inject;

import circus.robocalc.robochart.Type;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.WildcardArgument;

/**
 * Finds the RoboChart type of bindings by traversing the object graph back to where they were
 * used.
 *
 * @author Matt Windsor
 */
public record BindingTypeFinder(TopicParameterFinder tx) {
	// TODO(@MattWindsor91): expose this through the metamodel.

	/**
	 * Constructs a binding type finder.
	 *
	 * @param tx a topic parameter finder.
	 */
	@Inject
	public BindingTypeFinder {
	}

	/**
	 * Gets the type of a {@link Binding} by traversing its container(s).
	 * <p>
	 * For instance, a binding within a {@link MessageSpec} will be resolved against the parameters of
	 * its corresponding {@link MessageTopic}.
	 *
	 * @param b the binding whose type is to be resolved.
	 * @return the RoboChart type of the binding.
	 */
	public Type getType(Binding b) {
		final var parent = b.eContainer();

		if (parent instanceof WildcardArgument arg) {
			return typeFromWildcardArgument(arg);
		}

		// Add other locations for bindings here.

		throw new UnsupportedOperationException("Unsupported binding container: " + parent);
	}

	private Type typeFromWildcardArgument(WildcardArgument arg) {
		final var parent = arg.eContainer();

		if (parent instanceof MessageSpec spec) {
			return typeFromMessageSpec(spec, arg);
		}

		// If WildcardArguments can ever come from things other than message
		// specs, add code for them here.

		throw new UnsupportedOperationException("Unsupported wildcard argument container: " + parent);
	}

	private Type typeFromMessageSpec(MessageSpec spec, WildcardArgument arg) {
		final var index = spec.getArguments().indexOf(arg);
		if (index == -1) {
			throw new IndexOutOfBoundsException("couldn't find index of argument: " + arg);
		}
		final var param = tx.paramTypes(spec.getTopic()).skip(index).findFirst();
		if (param.isEmpty()) {
			throw new IndexOutOfBoundsException("argument index out of range: %d".formatted(index));
		}
		return param.get();
	}
}
