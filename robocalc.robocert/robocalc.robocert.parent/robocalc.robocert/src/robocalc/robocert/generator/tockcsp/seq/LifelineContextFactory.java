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
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.seq;

import java.util.List;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ContextActor;
import robocalc.robocert.model.robocert.Sequence;

/**
 * Creates, from a sequence, a series of lifeline contexts for use in
 * generating individual lifelines.
 * 
 * @author Matt Windsor
 */
public class LifelineContextFactory {
	/**
	 * Creates contexts for each semantics-visible lifeline in the given
	 * sequence.
	 * 
	 * Not all lifelines are visible; any that form a context do not appear in
	 * the semantics as they are considered to be the CSP environment.
	 * 
	 * @param s the sequence for which we are creating contexts.
	 *
	 * @return the list of contexts.
	 */
	public List<LifelineContext> createContexts(Sequence s) {
		return s.getLifelines().parallelStream().filter(this::actorVisibleInSemantics).map(this::createContext).toList();
	}
	
	private boolean actorVisibleInSemantics(Actor a) {
		// This may change in future.
		return !(a instanceof ContextActor);
	}
	
	private LifelineContext createContext(Actor a) {
		// TODO(@MattWindsor91): this dependency will flip around as the
		// message set generator becomes dependent on the context.
		var alphabet = MessageSetGenerator.QUALIFIED_UNIVERSE_NAME;
		
		// This will expand in future.
		return new LifelineContext(a, alphabet);
	}
}