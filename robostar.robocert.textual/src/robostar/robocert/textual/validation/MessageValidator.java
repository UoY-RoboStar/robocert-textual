/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.validation;

import com.google.inject.Inject;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robostar.robocert.Actor;
import robostar.robocert.EventTopic;
import robostar.robocert.Message;
import robostar.robocert.OperationTopic;
import robostar.robocert.RoboCertPackage.Literals;
import robostar.robocert.World;
import robostar.robocert.util.resolve.EventResolver;

/**
 * Validates aspects of message specifications.
 *
 * @author Matt Windsor
 */
public class MessageValidator extends AbstractDeclarativeValidator {

  // TODO(@MattWindsor91): fix the below
  public static final String EDGE_ACTORS_INDISTINCT = "edgeActorsIndistinct";
  //
  // Message
  //
  public static final String OPERATION_NEEDS_CONTEXT = "operationNeedsContext";
  public static final String OPERATION_FROM_CONTEXT = "operationFromContext";

  public static final String EVENT_TOPIC_HAS_CONNECTION = "SMTp2";

  @Inject
  private EventResolver eventResolver;

  @Check
  public void checkEventTopicHasConnection(Message m) {
    if (!(m.getTopic() instanceof EventTopic e)) {
      return;
    }
    final var from = m.getFrom();
    final var to = m.getTo();
    final var candidates = eventResolver.resolve(e, from, to)
        .collect(Collectors.toUnmodifiableSet());

    if (candidates.isEmpty()) {
      if (!m.isOutbound()) {
        // in outbound situations, we don't necessarily have access to the World's connections,
        // so it isn't a well-formedness error to not find a connection.
        error("Event topic is not outbound and does not correspond to any connection",
            Literals.MESSAGE__TOPIC, EVENT_TOPIC_HAS_CONNECTION);
      }
      return;
    }

    if (1 < candidates.size()) {
      error("Event topic corresponds to too many connections", Literals.MESSAGE__TOPIC,
          EVENT_TOPIC_HAS_CONNECTION);
    }
    // TODO(@MattWindsor91):
  }

  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  /**
   * Checks that an edge's general flow is valid.
   *
   * @param s the spec to check.
   */
  @Check
  public void checkEdgeFlow(Message s) {
    if (EcoreUtil.equals(s.getFrom(), s.getTo())) {
      error(
          "A message cannot mention the same actor at both endpoints",
          Literals.MESSAGE__FROM,
          EDGE_ACTORS_INDISTINCT);
    }
  }

  /**
   * Checks that the flow of an operation message is valid.
   *
   * @param s the spec to check.
   */
  @Check
  public void checkMessageOperationFlow(Message s) {
    // This check is only relevant for operation topics.
    if (!(s.getTopic() instanceof OperationTopic)) {
      return;
    }

    if (isContext(s.getFrom())) {
      error(
          "Operation messages must not originate from a context",
          Literals.MESSAGE__FROM,
          OPERATION_FROM_CONTEXT);
    }
    if (!isContext(s.getTo())) {
      error(
          "Operation messages must call into a context",
          Literals.MESSAGE__TO,
          OPERATION_NEEDS_CONTEXT);
    }

    // TODO(@MattWindsor91): I think that scoping rules will ensure that
    // there cannot be any operation messages into things that can't be
    // called into from this target, but I'm unsure.
  }

  private boolean isContext(Actor a) {
    return a instanceof World;
  }
}
