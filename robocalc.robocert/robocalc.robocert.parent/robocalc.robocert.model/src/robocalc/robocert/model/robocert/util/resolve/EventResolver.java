/*******************************************************************************
 * Copyright (c) 2022 University of York and others
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

package robocalc.robocert.model.robocert.util.resolve;

import circus.robocalc.robochart.Connection;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EventTopic;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public interface EventResolver {

  /**
   * Resolves an event to a candidate stream of connections.
   *
   * <p>This stream may contain zero, one, or many connections; typically anything other than one
   * is a well-formedness violation.
   *
   * @param topic the topic of the event to look up.
   * @param from  the from-actor of the event's message.
   * @param to    the to-actor of the event's message.
   * @return the stream of candidate connections.
   */
  Stream<Connection> resolve(EventTopic topic, Actor from, Actor to);
}
