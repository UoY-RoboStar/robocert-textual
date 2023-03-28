/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.scoping;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import robostar.robocert.*;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.resolve.DefinitionResolver;
import robostar.robocert.util.resolve.EndIndex;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;

/**
 * Scoping logic for message topics.
 *
 * @author Matt Windsor
 */
public record TopicScopeProvider(CTimedGeneratorUtils gu, DefinitionResolver defRes,
                                 MessageEndNodeResolver endRes, GroupFinder groupFinder) {

  @Inject
  public TopicScopeProvider {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(defRes);
    Objects.requireNonNull(endRes);
    Objects.requireNonNull(groupFinder);
  }

  /**
   * Calculates the scope of events available to the given topic.
   *
   * @param t        the topic for which we are getting scoping information
   * @param eventEnd the end of the message at which the event being resolved is located
   * @return the scope (can be null)
   */
  public IScope eventScope(EventTopic t, EndIndex eventEnd) {
    return groupFinder.findOnObject(t).map(g -> eventScopeFor(g, t, eventEnd))
        .orElse(IScope.NULLSCOPE);
  }

  private IScope eventScopeFor(SpecificationGroup grp, EventTopic t, EndIndex eventEnd) {
    final var actors = grp.getActors();

    final var msg = t.getMessage();
    final var tgt = grp.getTarget();

    final var end = scopeEnd(msg, tgt, eventEnd);
    return Scopes.scopeFor(endCandidates(end.of(msg), actors, gu::allEvents));
  }

  private EndIndex scopeEnd(Message msg, Target tgt, EndIndex eventEnd) {
    // TODO(@MattWindsor91): this seems to duplicate a lot of the logic in the metamodel resolvers.

    // For component messages, we take scope from whichever side of the message we are resolving.
    if (!msg.isOutbound()) {
      return eventEnd;
    }

    // For outbound messages, we usually take scope from whichever side is NOT the gate.
    final var usualEnd = msg.getTo().isGate() ? EndIndex.From : EndIndex.To;

    // However, there is a corner-case: the appropriate scope on a module target is the robotic
    // platform (i.e. the gate), and not the contents of the module (ie, the TargetActor).
    return usualEnd.oppositeIf(tgt instanceof ModuleTarget);
  }

  /**
   * Calculates the scope of operations available to the given topic.
   *
   * @param t the topic for which we are getting scoping information.
   * @return the scope (may be null).
   */
  public IScope getOperationScope(OperationTopic t) {
    return groupFinder.findOnObject(t).map(g -> operationScopeFor(g, t)).orElse(IScope.NULLSCOPE);
  }

  private IScope operationScopeFor(SpecificationGroup grp, OperationTopic t) {
    final var actors = grp.getActors();

    // Operation scopes are always defined on the 'from' end, because (as of writing),
    // all operations are calls to the robotic platform (gate).
    return Scopes.scopeFor(endCandidates(t.getMessage().getFrom(), actors, gu::allOperations));
  }

  private <T extends EObject> Set<T> endCandidates(MessageEnd e, List<Actor> actors,
      Function<Context, List<T>> selector) {
    final var nodes = endRes.resolve(e, actors);
    return nodes.flatMap(n -> selectToStream(n, selector)).collect(Collectors.toSet());
  }

  private <T extends EObject> Stream<T> selectToStream(ConnectionNode n,
      Function<Context, List<T>> selector) {
    final var ctx = defRes.context(n);
    return selector.apply(ctx).stream();
  }
}
