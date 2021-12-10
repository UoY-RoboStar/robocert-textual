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
import circus.robocalc.robochart.TypedNamedElement;
import java.util.Optional;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.OperationTopic;

/**
 * Finds and enumerates all parameters (or parameter types) available on a topic.
 *
 * @author Matt Windsor
 */
public class TopicParameterFinder {

	/**
	 * Streams all of the parameter types in a topic.
	 * <p>
	 * An event has at most one type, depending on if it is typed or not.  Operations may have
	 * multiple types.
	 *
	 * @param t the topic for which we are getting parameter types.
	 * @return the parameter types of the topic.
	 */
	public Stream<Type> paramTypes(MessageTopic t) {
		// NOTE(@MattWindsor91): we used to have code here for returning a stream of all parameters,
		// but it no longer seems useful.  May need to resurrect it in the future.

		if (t instanceof EventTopic e) {
			return Optional.ofNullable(e.getEvent().getType()).stream();
		}
		if (t instanceof OperationTopic o) {
			return o.getOperation().getParameters().stream().map(TypedNamedElement::getType);
		}
		throw new IllegalArgumentException("unsupported topic for parameter getting: %s".formatted(t));
	}
}
