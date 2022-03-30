/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial implementation
 ********************************************************************************/
package robocalc.robocert.scoping;

import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.util.ActorContextFinder;

/**
 * Scoping logic for message topics.
 *
 * @author Matt Windsor
 */
public record TopicScopeProvider(
    CTimedGeneratorUtils gu,
    ActorContextFinder acf) {

  @Inject
  public TopicScopeProvider {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(acf);
  }

  /**
   * Calculates the scope of operations available to the given topic.
   *
   * @param t      the topic for which we are getting scoping information.
   * @param isFrom whether we are looking at eFrom.
   * @return the scope (may be null).
   */
  public IScope getEventScope(EventTopic t, boolean isFrom) {
    return scope(t, isFrom, gu::allEvents);
  }

  /**
   * Calculates the scope of operations available to the given topic.
   *
   * @param t the topic for which we are getting scoping information.
   * @return the scope (may be null).
   */
  public IScope getOperationScope(OperationTopic t) {
    return scope(t, true, gu::allOperations);
  }

  private <T extends EObject> IScope scope(MessageTopic t, boolean isFrom,
      Function<Context, List<T>> selector) {
    final var msg = t.getMessage();
    final var candidates = actorCandidates(isFrom ? msg.getFrom() : msg.getTo(), selector);
    return Scopes.scopeFor(candidates);
  }

  private <T extends EObject> Set<T> actorCandidates(
      Actor a, Function<Context, List<T>> selector) {
    return acf.contexts(a).map(selector).flatMap(List<T>::stream).collect(Collectors.toSet());
  }
}
