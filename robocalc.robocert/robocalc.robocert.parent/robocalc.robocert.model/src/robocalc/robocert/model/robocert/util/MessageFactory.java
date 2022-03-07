/*******************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import com.google.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.ValueSpecification;

/**
 * High-level factory for message-related objects.
 *
 * @author Matt Windsor
 */
public class MessageFactory {
  @Inject private RoboCertFactory rc;

  /**
   * Constructs a message spec with the given topic, edge, and arguments.
   *
   * @param from from-actor to use for the message spec.
   * @param to to-actor to use for the message spec.
   * @param topic topic to use for the message spec.
   * @param args arguments to use for the message spec.
   * @return the specification.
   */
  public Message spec(Actor from, Actor to, MessageTopic topic, ValueSpecification... args) {
    return spec(from, to, topic, Arrays.asList(args));
  }

  /**
   * Constructs a message spec with the given topic, edge, and argument collection.
   *
   * @param from from-actor to use for the message spec.
   * @param to to-actor to use for the message spec.
   * @param topic the topic to use for the message spec.
   * @param args the arguments to use for the message spec.
   * @return the specification.
   */
  public Message spec(
      Actor from, Actor to, MessageTopic topic, Collection<? extends ValueSpecification> args) {
    final var it = rc.createMessage();
    it.setFrom(from);
    it.setTo(to);
    it.setTopic(topic);
    it.getArguments().addAll(args);
    return it;
  }

  /**
   * Constructs an event topic with the given event.
   *
   * @param e the event to use.
   * @return the event topic.
   */
  public MessageTopic eventTopic(Event e) {
    final var it = rc.createEventTopic();
    it.setEvent(e);
    return it;
  }

  /**
   * Constructs an operation topic with the given operation.
   *
   * @param o the signature of the operation to use.
   * @return the event topic.
   */
  public MessageTopic opTopic(OperationSig o) {
    final var it = rc.createOperationTopic();
    it.setOperation(o);
    return it;
  }

  /** @return a target actor. */
  public Actor targetActor() {
    return rc.createTargetActor();
  }

  /** @return a world. */
  public Actor world() {
    return rc.createWorld();
  }

  /** @return a list containing all actors defined on a module target. */
  public List<Actor> systemActors() {
    return List.of(targetActor(), world());
  }
}
