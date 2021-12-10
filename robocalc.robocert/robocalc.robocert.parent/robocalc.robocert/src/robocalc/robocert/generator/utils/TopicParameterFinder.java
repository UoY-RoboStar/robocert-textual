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

import circus.robocalc.robochart.TypedNamedElement;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.OperationTopic;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.Type;
import org.eclipse.xtext.EcoreUtil2;

/**
 * Finds and enumerates all parameters (or parameter types) available on a topic.
 *
 * @author Matt Windsor
 */
public record TopicParameterFinder(RoboChartFactory rc) {

	/**
	 * Constructs a topic parameter finder.
	 *
	 * @param rc RoboChart factory, used for producing pseudo-parameters for events.
	 */
	@Inject
	public TopicParameterFinder {
	}

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
		return paramInner(t, x -> x, TypedNamedElement::getType);
	}

	/**
	 * Streams all of the parameters in a topic.
	 * <p>
	 * An event has at most one parameter, depending on if it is typed or not.  This parameter is
	 * synthetic (we create it from the event type for convenience).  Operations may have multiple
	 * parameters, and they all correspond to real RoboChart model objects.
	 *
	 * @param t the topic for which we are getting parameters.
	 * @return the parameters of the topic.
	 */
	public Stream<Parameter> params(MessageTopic t) {
		return paramInner(t, this::eventTypeToParam, x -> x);
	}

	private <T> Stream<T> paramInner(MessageTopic t, Function<Type, T> eaccept,
			Function<Parameter, T> oaccept) {
		if (t instanceof EventTopic e) {
			return Optional.ofNullable(e.getEvent().getType()).stream().map(eaccept);
		}
		if (t instanceof OperationTopic o) {
			return o.getOperation().getParameters().stream().map(oaccept);
		}
		throw new IllegalArgumentException("unsupported topic for parameter getting: %s".formatted(t));
	}

	/**
	 * Wraps an event type into a parameter, so that the rest of the generator can treat it as one.
	 *
	 * @param t the type to expand.
	 * @return the type as a parameter.
	 */
	private Parameter eventTypeToParam(Type t) {
		final var p = rc.createParameter();
		p.setName(EVENT_PARAM_NAME);
		/* We can't just say 'type = t' here: it introduces interesting
		 * aliasing issues that cause the original type to be nullified at
		 * strange times.
		 */
		p.setType(EcoreUtil2.copy(t));
		return p;
	}

	/**
	 * The arbitrary name assigned to event-types-as-parameters.
	 */
	static final String EVENT_PARAM_NAME = "x";
}
