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

import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.Target;

import org.eclipse.xtext.scoping.IScope;
import robocalc.robocert.generator.utils.TargetExtensions;
import com.google.inject.Inject;
import org.eclipse.xtext.scoping.Scopes;
import circus.robocalc.robochart.Context;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.MessageTopic;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import robocalc.robocert.generator.utils.EObjectExtensions;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Scoping logic for message topics.
 * 
 * @author Matt Windsor
 */
public class TopicScopeProvider {
	@Inject private CTimedGeneratorUtils gu;
	@Inject private TargetExtensions tx;
	@Inject private EObjectExtensions ex;

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
	 * @param it  the topic for which we are getting scoping information.
	 * 
	 * @return the scope (may be null).
	 */
	public IScope getOperationScope(OperationTopic t) {
		return scope(t, gu::allOperations);
	}
	
	private <T extends EObject> IScope scope(MessageTopic it, Function<Context, ArrayList<T>> selector) {
		var target = ex.getTargetOfParentGroup(it);
		return (target == null) ? null : scopeOfTarget(target, selector);
	}

	private <T extends EObject> IScope scopeOfTarget(Target target, Function<Context, ArrayList<T>> selector) {
		var set = tx.contexts(target).map(selector).flatMap(ArrayList<T>::stream).collect(Collectors.toSet());
		return Scopes.scopeFor(set);
	}
}