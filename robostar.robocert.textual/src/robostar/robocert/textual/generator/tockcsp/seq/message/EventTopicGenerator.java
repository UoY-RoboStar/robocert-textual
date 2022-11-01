/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq.message;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import java.util.Objects;
import javax.inject.Inject;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.EventTopic;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.util.resolve.EventResolver;

/**
 * Generates CSP-M for message channels using event topics.
 *
 * @author Matt Windsor
 */
public record EventTopicGenerator(CSPStructureGenerator csp, CTimedGeneratorUtils gu,
                                  EventResolver eventResolver, ActorNamespaceResolver nsResolver) {

  /**
   * Constructs an event topic generator.
   *
   * @param csp           CSP structure generator, used mainly for constructing namespaced
   *                      references.
   * @param gu            RoboChart generator utilities.
   * @param eventResolver Resolves various aspects of events.
   * @param nsResolver    Resolves actors into their namespaces.
   */
  @Inject
  public EventTopicGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
    Objects.requireNonNull(eventResolver);
    Objects.requireNonNull(nsResolver);
  }

  /**
   * Generates channel CSP-M for an event topic.
   *
   * @param e    the topic for which we are generating a channel.
   * @param from the from-actor of the topic's message.
   * @param to   the to-actor of the topic's message.
   * @return the resulting CSP-M.
   */
  public CharSequence generateEvent(EventTopic e, Actor from, Actor to) {
    final var einfo = resolveEvent(e, from, to);

    final var ns = nsResolver.namespace(einfo.actor);
    final var channelName = csp.namespaced(ns, gu.eventId(einfo.event));

    return String.join(".", channelName, einfo.dir.toString());
  }

  private EventInfo resolveEvent(EventTopic e, Actor from, Actor to) {
    // We only use efrom/eto for event naming, so it's ok to do this.
    final var efrom = e.getEfrom();
    final var eto = Objects.requireNonNullElse(e.getEto(), efrom);

    // Firstly, do we have an outbound connection?
    // If so, we always resolve in favour of the the non-world side.
    if (from instanceof World) {
      return new EventInfo(Direction.IN, to, eto);
    }
    if (to instanceof World) {
      return new EventInfo(Direction.OUT, from, efrom);
    }
    // We're in a multi-component situation.
    if (from instanceof ComponentActor f) {
      final var fnode = f.getNode();

      return eventResolver.resolve(e, from, to).map(conn -> {
        if (fnode != conn.getFrom() && fnode != conn.getTo()) {
          throw new IllegalArgumentException(
              "from-node of message didn't match either end of its connection");
        }

        // Following rule 15 of the RoboChart semantics, we usually take the actor and event
        // representing the 'from' of the connection.
        //
        // If we matched the 'from' of the topic to the 'to' node of the connection, we have a
        // bidirectional connection that matched backwards.  This bit of code reverses the actor
        // and direction so that the event still names the from-node, which is important once we
        // start fusing together synchronous channels.
        //
        // TODO(@MattWindsor91): handle async properly?.

        final var dir = fnode == conn.getFrom() ? Direction.OUT : Direction.IN;
        assert (dir == Direction.OUT || conn.isBidirec());
        final var base = dir == Direction.OUT ? from : to;

        return new EventInfo(dir, base, conn.getEfrom());
      }).findAny().orElseThrow();
    }

    throw new IllegalArgumentException("unsupported actors: %s, %s".formatted(from, to));
  }

  private record EventInfo(Direction dir, Actor actor, Event event) {

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
