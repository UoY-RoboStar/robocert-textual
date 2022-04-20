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

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import java.util.Objects;
import javax.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.ModuleTarget;
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
    final var dir = inferDirection(from, to);

    // We only use efrom/eto for event naming, so it's ok to do this.
    final var efrom = e.getEfrom();
    final var eto = Objects.requireNonNullElse(e.getEto(), efrom);
    final var eTarget = dir == Direction.IN ? eto : efrom;

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
    return gu.processId(namespaceRoot(base));
  }

  private EObject namespaceRoot(Actor base) {
    if (!(base instanceof World)) {
      // In both component and target actor cases, we want to get the namespace of the target.
      // For target actors, this is self-evident; for component actors, it's a little subtle:
      // the semantics we use for collection targets renames things to the target's namespace even
      // if they belong to components, and so the namespace needs to agree.

      // If the target is a module, `resolve` would give us the list of components instead of
      // the module, so we do things slightly indirectly.
      // TODO(@MattWindsor91): is that even the right behaviour?
      final var ot = nodeResolver.target(base);
      if (ot.isPresent()) {
        final var t = ot.get();
        if (t instanceof InModuleTarget m) {
          return m.getModule();
        }
        if (t instanceof ModuleTarget m) {
          return m.getModule();
        }
        return nodeResolver.resolveTarget(t).findAny().orElseThrow();
      }
    }

    // For worlds and everything else, fallback to trying to resolve the actor to a connection node.
    // TODO(@MattWindsor91): do we even ever *reach* World here?
    return nodeResolver.resolve(base).findAny().orElseThrow();
  }

  private Direction inferDirection(Actor from, Actor to) {
    // Firstly, do we have an outbound connection?
    if (from instanceof World) {
      // Use 'to' as namespace, 'eto' as event, and the direction is 'in'
      return Direction.IN;
    }
    if (to instanceof World) {
      // Use 'from' as namespace, 'efrom' as event, and the direction is 'out'
      return Direction.OUT;
    }
    // We're in a multi-component situation.
    // Following rule 15 of the RoboChart semantics, we usually take the namespace of the 'to' actor
    // and emit as IN.

    // TODO(@MattWindsor91): synchronous semantics
    return Direction.IN;
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
