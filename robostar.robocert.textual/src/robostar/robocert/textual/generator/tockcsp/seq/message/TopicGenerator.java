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
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.EventTopic;
import robostar.robocert.MessageTopic;
import robostar.robocert.ModuleTarget;
import robostar.robocert.OperationTopic;
import robostar.robocert.TargetActor;
import robostar.robocert.World;
import robostar.robocert.util.ActorContextFinder;
import robostar.robocert.util.ActorNodeResolver;
import robostar.robocert.util.resolve.EventResolver;

/**
 * Generates CSP-M for message channels using message topics.
 * <p>
 * This part of the generator does not take into account arguments, but it has to take into account
 * both the topic and the edge of the message.
 *
 * @author Matt Windsor
 */
public record TopicGenerator(CSPStructureGenerator csp,
                             ActorNamespaceResolver nsResolver,
                             EventTopicGenerator etGenerator,
                             ActorContextFinder ctxResolver) {

  /**
   * Constructs a channel generator.
   *
   * @param csp          CSP structure generator, used mainly for constructing namespaced
   *                     references.
   * @param nsResolver Resolves actors into namespaces.
   * @param etGenerator Generates topics for events.
   * @param ctxResolver  Resolves actors into contexts.
   */
  @Inject
  public TopicGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(nsResolver);
    Objects.requireNonNull(etGenerator);
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
    // SEMANTICS: [[-]]

    if (topic instanceof EventTopic e) {
      // Events are complicated enough to delegate to a separate generator.
      return etGenerator.generateEvent(e, from, to);
    }
    if (topic instanceof OperationTopic o) {
      return generateOp(o, from, to);
    }
    throw new IllegalArgumentException(
        "unsupported topic for channel generation: %s".formatted(topic));
  }

  private CharSequence generateOp(OperationTopic o, Actor from, Actor to) {
    if (!(to instanceof World)) {
      throw new IllegalArgumentException(
          "WFC SMT1: to-actor of an operation must be a World, got %s".formatted(to));
    }

    return csp.namespaced(nsResolver.namespace(from), o.getOperation().getName() + "Call");
  }
}
