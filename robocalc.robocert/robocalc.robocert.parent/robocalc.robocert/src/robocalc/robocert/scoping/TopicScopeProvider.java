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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.OperationTopic;

/**
 * Scoping logic for message topics.
 *
 * @author Matt Windsor
 */
public class TopicScopeProvider {
	private CTimedGeneratorUtils gu;
	private ActorContextFinder acf;

	@Inject
	public TopicScopeProvider(CTimedGeneratorUtils gu, ActorContextFinder acf) {
		this.gu = gu;
		this.acf = acf;
	}

	/**
	 * Calculates the scope of operations available to the given topic.
	 *
	 * @param t the topic for which we are getting scoping information.
	 *
	 * @return the scope (may be null).
	 */
	public IScope getEventScope(EventTopic t) {
		return scope(t, gu::allEvents);
	}

	/**
	 * Calculates the scope of operations available to the given topic.
	 *
	 * @param it the topic for which we are getting scoping information.
	 *
	 * @return the scope (may be null).
	 */
	public IScope getOperationScope(OperationTopic t) {
		return scope(t, gu::allOperations);
	}

	private <T extends EObject> IScope scope(MessageTopic t, Function<Context, List<T>> selector) {
		return Scopes.scopeFor(scopeSet(t, selector));
	}

	private <T extends EObject> Set<T> scopeSet(MessageTopic t, Function<Context, List<T>> selector) {
		var edge = t.getSpec().getEdge();
		var fromCandidates = actorCandidates(edge.getResolvedFrom(), selector);
		var toCandidates = actorCandidates(edge.getResolvedTo(), selector);

		// If both actors have a well-defined scope set, we want only the items
		// reachable from both; otherwise, just pick up those on the one
		// actor.
		if (fromCandidates.isEmpty())
			return toCandidates.orElse(Set.of());
		if (toCandidates.isEmpty())
			return fromCandidates.orElse(Set.of());
		return Sets.intersection(fromCandidates.get(), toCandidates.get());
	}

	private <T extends EObject> Optional<Set<T>> actorCandidates(Actor a, Function<Context, List<T>> selector) {
		return acf.contexts(a)
				.map((Stream<Context> x) -> x.map(selector).flatMap(List<T>::stream).collect(Collectors.toSet()));
	}
}