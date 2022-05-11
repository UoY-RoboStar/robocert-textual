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
package robostar.robocert.textual.validation;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import robostar.robocert.CollectionTarget;
import robostar.robocert.ComponentActor;
import robostar.robocert.RoboCertPackage;

/**
 * Validates aspects of actors.
 *
 * @author Matt Windsor
 */
public class ActorValidator extends AbstractDeclarativeValidator {
	@Override
	public void register(EValidatorRegistrar registrar) {
		// per discussion in ComposedChecks annotation documentation
	}

	public static final String NOT_A_COMPONENT = "notAComponent";

	/**
	 * Checks a component actor to make sure that it is pointing to a node directly
	 * enclosed within the target of the actor's group.
	 *
	 * @param c the component actor to check.
	 */
	@Check
	public void checkComponentMemberOfTarget(ComponentActor c) {
		final var target = c.getGroup().getTarget();
		final var node = c.getNode();

		if (target instanceof CollectionTarget ct) {
			if (ct.getComponents().stream().noneMatch(x -> EcoreUtil.equals(node, x)))
				error("Component must be a member of the target", RoboCertPackage.Literals.COMPONENT_ACTOR__NODE,
						 NOT_A_COMPONENT);
		}
		// TODO(@MattWindsor91): error here
	}
}
