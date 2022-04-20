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

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import java.util.Objects;
import javax.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.TargetActor;
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
   * @param csp          CSP structure generator, used mainly for constructing namespaced
   *                     references.
   * @param gu           RoboChart generator utilities.
   * @param nodeResolver Resolves actors into connection nodes.
   * @param ctxResolver  Resolves actors into contexts.
   */
  @Inject
  public TopicGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
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
    final var einfo = resolveEvent(e, from, to);
    final var channelName = csp.namespaced(namespace(einfo.actor), gu.eventId(einfo.event));
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
        // TODO(@MattWindsor91): handle async and bidirec.

        // Following rule 15 of the RoboChart semantics, we usually take the actor and event
        // representing the 'from' of the connection.

        if (fnode != conn.getFrom() && fnode != conn.getTo()) {
          throw new IllegalArgumentException("from-node of message didn't match either end of its connection");
        }

        return new EventInfo(fnode == conn.getFrom() ? Direction.OUT : Direction.IN, f, conn.getEfrom());
      }).findAny().orElseThrow();
    }

    throw new IllegalArgumentException("unsupported actors: %s, %s".formatted(from, to));
  }

  private record EventInfo(Direction dir, Actor actor, Event event) {}

  private CharSequence generateOp(OperationTopic o, Actor from, Actor to) {
    if (!(to instanceof World)) {
      throw new IllegalArgumentException(
          "WFC SMT1: to-actor of an operation must be a World, got %s".formatted(to));
    }

    return csp.namespaced(namespace(from), o.getOperation().getName() + "Call");
  }

  private CharSequence namespace(Actor base) {
    return gu.processId(namespaceRoot(base));
  }

  private EObject namespaceRoot(Actor base) {
    if (base instanceof TargetActor) {
      // In target actor cases, we want to get the namespace of the target.
      // This differs from the usual code path in one place:
      // if the target is a module, `resolve` would give us the list of components instead of
      // the module, so we do things slightly indirectly.
      // TODO(@MattWindsor91): is that even the right behaviour?
      final var ot = nodeResolver.target(base);
      if (ot.isPresent()) {
        final var t = ot.get();
        if (t instanceof ModuleTarget m) {
          return m.getModule();
        }
        return nodeResolver.resolveTarget(t).findAny().orElseThrow();
      }
    }

    return nodeResolver.resolve(base).findAny().orElseThrow();
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
