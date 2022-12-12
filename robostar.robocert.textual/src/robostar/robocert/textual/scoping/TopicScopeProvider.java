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
import robostar.robocert.util.resolve.node.EndpointNodeResolver;

/**
 * Scoping logic for message topics.
 *
 * @author Matt Windsor
 */
public record TopicScopeProvider(CTimedGeneratorUtils gu, DefinitionResolver defRes, EndpointNodeResolver endRes, GroupFinder groupFinder) {

  @Inject
  public TopicScopeProvider {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(defRes);
    Objects.requireNonNull(endRes);
    Objects.requireNonNull(groupFinder);
  }

  /**
   * Calculates the scope of operations available to the given topic.
   *
   * @param t       the topic for which we are getting scoping information.
   * @param isEfrom whether we are looking at eFrom.
   * @return the scope (may be null).
   */
  public IScope getEventScope(EventTopic t, boolean isEfrom) {
    final var msg = t.getMessage();
    final var from = msg.getFrom();
    final var to = msg.getTo();

    // For component messages, we take scope from whichever side of the message we are resolving.
    // For outbound messages, we can only resolve efrom, but need to work out which of the two
    // actors gives us the right scope. By default, we use whichever Actor isn't the World.
    if (msg.isOutbound()) {
      isEfrom = to.isWorld();
      // However, there is a corner-case: the appropriate scope on a module target is the robotic
      // platform (ie, the World), and not the contents of the module (ie, the TargetActor).
      final var tgt = groupFinder.findTarget(from);
      if (tgt.filter(x -> x instanceof ModuleTarget).isPresent()) {
        isEfrom = !isEfrom;
      }
    }

    return Scopes.scopeFor(endpointCandidates(isEfrom ? from : to, gu::allEvents));
  }

  /**
   * Calculates the scope of operations available to the given topic.
   *
   * @param t the topic for which we are getting scoping information.
   * @return the scope (may be null).
   */
  public IScope getOperationScope(OperationTopic t) {
    return Scopes.scopeFor(endpointCandidates(t.getMessage().getFrom(), gu::allOperations));
  }

  private <T extends EObject> Set<T> endpointCandidates(Endpoint e, Function<Context, List<T>> selector) {
    final var nodes = endRes.resolve(e);
    return nodes.flatMap(n -> selectToStream(n, selector)).collect(Collectors.toSet());
  }

  private <T extends EObject> Stream<T> selectToStream(ConnectionNode n, Function<Context, List<T>> selector) {
    final var ctx = defRes.context(n);
    return selector.apply(ctx).stream();
  }
}
