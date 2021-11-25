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
package robocalc.robocert.validation;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ContextActor;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.RoboCertPackage;
import robocalc.robocert.model.robocert.util.EdgeFactory;

/**
 * Validates aspects of message specifications.
 *
 * @author Matt Windsor
 */
public class MessageSpecValidator extends AbstractDeclarativeValidator {
	@Inject
	private EdgeFactory ef;
	
	@Override
	public void register(EValidatorRegistrar registrar) {
		// per discussion in ComposedChecks annotation documentation
	}

	//
	// MessageSpec
	//

	public static final String EDGE_ACTORS_INDISTINCT = "edgeActorsIndistinct";
	public static final String OPERATION_NEEDS_CONTEXT = "operationNeedsContext";
	public static final String OPERATION_FROM_CONTEXT = "operationFromContext";

	/**
	 * Checks that an edge's general flow is valid.
	 *
	 * @param s the spec to check.
	 */
	@Check
	public void checkEdgeFlow(MessageSpec s) {
		var e = s.getEdge();
		if (EcoreUtil.equals(e.getResolvedFrom(), e.getResolvedTo()))
			edgeError("A message cannot mention the same actor at both endpoints", EDGE_ACTORS_INDISTINCT);
	}

	/**
	 * Checks that the flow of an operation message is valid.
	 *
	 * @param s the spec to check.
	 */
	@Check
	public void checkMessageSpecOperationFlow(MessageSpec s) {
		// This check is only relevant for operation topics.
		if (!(s.getTopic() instanceof OperationTopic))
			return;
		
		var e = ef.resolvedEdge(s.getEdge());
		if (isContext(e.getFrom()))
			edgeError("Operation messages must not originate from a context", OPERATION_FROM_CONTEXT);
		if (!isContext(e.getTo()))
			edgeError("Operation messages must call into a context", OPERATION_NEEDS_CONTEXT);
		
		// TODO(@MattWindsor91): I think that scoping rules will ensure that
		// there cannot be any operation messages into things that can't be
		// called into from this target, but I'm unsure.
	}

	private void edgeError(String string, String code) {
		error(string, RoboCertPackage.Literals.MESSAGE_SPEC__EDGE, code);
	}

	private boolean isContext(Actor a) {
		return a instanceof ContextActor;
	}
}
