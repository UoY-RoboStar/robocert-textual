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
package robocalc.robocert.generator.tockcsp.seq.message;

import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import java.util.Objects;
import javax.inject.Inject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.util.ActorContextFinder;
import robocalc.robocert.model.robocert.util.ActorNodeResolver;
import robocalc.robocert.model.robocert.util.EventResolver;

/**
 * Generates CSP-M for message channels using message topics.
 * <p>
 * This part of the generator does not take into account arguments, but it has to take into account
 * both the topic and the edge of the message.
 *
 * @author Matt Windsor
 */
public record TopicGenerator(CSPStructureGenerator csp, CTimedGeneratorUtils gu,
                             EventResolver eventResolver, ActorNodeResolver nodeResolver,
                             ActorContextFinder ctxResolver) {

  /**
   * Constructs a channel generator.
   *
   * @param csp           CSP structure generator, used mainly for constructing namespaced
   *                      references.
   * @param gu            RoboChart generator utilities.
   * @param eventResolver Resolves events into connections.
   * @param nodeResolver  Resolves actors into connection nodes.
   * @param ctxResolver   Resolves actors into contexts.
   */
  @Inject
  public TopicGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
    Objects.requireNonNull(eventResolver);
    Objects.requireNonNull(nodeResolver);
    Objects.requireNonNull(ctxResolver);
  }

  /**
   * Generates channel CSP-M for a topic.
   *
   * @param topic the topic for which we are generating a channel.
   * @param from  the from-actor of the topic's message.
   * @param to    the to-actor of the topic's message.
   * @return the resulting CSP-M.
   */
  public CharSequence generate(MessageTopic topic, Actor from, Actor to) {
    if (topic instanceof EventTopic e) {
      return generateEvent(e, from, to);
    }
    if (topic instanceof OperationTopic o) {
      return generateOp(o, from, to);
    }
    throw new IllegalArgumentException(
        "unsupported topic for channel generation: %s".formatted(topic));
  }

  private CharSequence generateEvent(EventTopic e, Actor from, Actor to) {
    // Instead of resolving events directly from the topic and actors, we use
    // a separate step to pull out one (and hopefully one) Connection that matches them, and use
    // the information from that.
    //
    // We don't use the from/to from the connection, because the connection might be bidirectional
    // and we might be using it in the reverse direction from its definition.
    final var connection = eventResolver.resolve(e, from, to).findAny().orElseThrow();

    // TODO(@MattWindsor91): work out if this is right in terms of bidirectionals.

    final var dir = inferDirection(to);

    final var eTarget = dir == Direction.IN ? connection.getEto() : connection.getEfrom();

    // TODO(@MattWindsor91): this could do with being closer to the logic in the RoboChart
    // statement generator.
    final var nsTarget = dir == Direction.IN ? to : from;
    final var channelName = csp.namespaced(namespace(nsTarget), gu.eventId(eTarget));
    return String.join(".", channelName, dir.toString());
  }

  private CharSequence generateOp(OperationTopic o, Actor from, Actor to) {
    if (!(to instanceof World)) {
      throw new IllegalArgumentException(
          "WFC SMT1: to-actor of an operation must be a World, got %s".formatted(to));
    }

    return csp.namespaced(namespace(from), o.getOperation().getName() + "Call");
  }

  private CharSequence namespace(Actor base) {
    // TODO(@MattWindsor91): this doesn't seem quite right.
    final var pbase = nodeResolver.resolveHandlingTargetModules(base).findAny().orElseThrow();
    return gu.processId(pbase);
  }

  private Direction inferDirection(Actor to) {
    // Following rule 15 of the RoboChart semantics, we usually take the namespace of the 'to' actor
    // and emit as IN.  There is one exception, namely if the 'to' points to a robotic platform.

    // TODO(@MattWindsor91): synchronous semantics

    final var isOut = nodeResolver.resolve(to).anyMatch(RoboticPlatformDef.class::isInstance);
    return isOut ? Direction.OUT : Direction.IN;
  }

  /**
   * Enumeration of directions of event channels.
   */
  private enum Direction {
    IN, OUT;

    @Override
    public String toString() {
      return switch (this) {
        case IN -> "in";
        case OUT -> "out";
      };
    }
  }
}
