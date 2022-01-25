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

import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.RoboCertPackage;
import robocalc.robocert.model.robocert.SequenceGroup;
import robocalc.robocert.model.robocert.TargetActor;

/**
 * Validates aspects of sequence groups.
 *
 * @author Matt Windsor
 */
public class SequenceGroupValidator extends AbstractDeclarativeValidator {
	@Override
	public void register(EValidatorRegistrar registrar) {
		// per discussion in ComposedChecks annotation documentation
	}

	// TODO(@MattWindsor91): systematic codes
	public static final String SMA_NEEDS_SYSTEM = "smaNeedsSystem";
	public static final String SYS_COMPONENTS = "sysComponents";
	public static final String SYS_NEEDS_ONE_SMA = "sysNeedsOneSMA";
	public static final String SYS_NEEDS_ONE_CONTEXT = "sysNeedsOneContext";

	public static final String TOO_MANY_CONTEXTS = "tooManyContexts";

	/**
	 * Checks that the counts of actors of certain types are valid if the sequence
	 * group has a module target.
	 *
	 * This composes with checkActorCounts.
	 *
	 * @param g the sequence group to check.
	 */
	@Check
	public void checkModuleTargetActorCounts(SequenceGroup g) {
		// TODO(@MattWindsor91): module target -> component target
		if (hasNonModuleTarget(g))
			return;

		if (1 != countActors(g, TargetActor.class))
			actorError("There must be precisely one target actor", SYS_NEEDS_ONE_SMA);

		if (hasActors(g, ComponentActor.class))
			actorError("System sequence groups cannot have components; use 'module'", SYS_COMPONENTS);

		if (1 != countActors(g, World.class))
			actorError("There must be precisely one context actor", SYS_NEEDS_ONE_CONTEXT);
	}

	/**
	 * Checks that the counts of actors of certain types are valid.
	 *
	 * This contains the checks common to all group targets.
	 *
	 * @param g the sequence group to check.
	 */
	@Check
	public void checkActorCounts(SequenceGroup g) {
		if (hasNonModuleTarget(g) && hasActors(g, TargetActor.class))
			actorError("Only module groups can have module actors", SMA_NEEDS_SYSTEM);

		if (1 < countActors(g, World.class))
			actorError("At most one actor in a sequence group can be the context", TOO_MANY_CONTEXTS);
	}

	private void actorError(String string, String code) {
		error(string, RoboCertPackage.Literals.SEQUENCE_GROUP__ACTORS, code);
	}

	//
	// Utility functions
	//

	// TODO(@MattWindsor91): I think these are used/useful/duplicated elsewhere?

	private boolean hasNonModuleTarget(SequenceGroup g) {
		return !(g.getTarget() instanceof ModuleTarget);
	}

	private boolean hasActors(SequenceGroup g, Class<? extends Actor> clazz) {
		return g.getActors().parallelStream().anyMatch(clazz::isInstance);
	}

	private long countActors(SequenceGroup g, Class<? extends Actor> clazz) {
		return g.getActors().parallelStream().filter(clazz::isInstance).count();
	}
}
