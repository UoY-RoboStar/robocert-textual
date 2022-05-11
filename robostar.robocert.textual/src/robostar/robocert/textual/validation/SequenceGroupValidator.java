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

import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.ComponentTarget;
import robostar.robocert.RoboCertPackage;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.TargetActor;
import robostar.robocert.World;

/**
 * Validates aspects of sequence groups.
 *
 * @author Matt Windsor
 */
public class SequenceGroupValidator extends AbstractDeclarativeValidator {
  // TODO(@MattWindsor91): systematic codes
  public static final String SMA_NEEDS_SYSTEM = "smaNeedsSystem";
  public static final String SYS_COMPONENTS = "sysComponents";
  public static final String SYS_NEEDS_ONE_SMA = "sysNeedsOneSMA";
  public static final String TOO_MANY_CONTEXTS = "tooManyContexts";

  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  /**
   * Checks that the counts of target actors makes sense for this target.
   *
   * <p>This composes with checkActorCounts.
   *
   * @param group the sequence group to check.
   */
  @Check
  public void checkTargetActorCount(SpecificationGroup group) {

    if (!hasComponentTarget(group) && hasActors(group, TargetActor.class))
      actorError("Only component targets can have target actors", SMA_NEEDS_SYSTEM);

    if (1 < countActors(group, TargetActor.class))
      actorError("There can be at most one target actor", SYS_NEEDS_ONE_SMA);
  }

  /**
   * Checks that the counts of actors of certain types are valid.
   *
   * <p>This contains the checks common to all group targets.
   *
   * @param group the sequence group to check.
   */
  @Check
  public void checkActorCounts(SpecificationGroup group) {
    if (hasComponentTarget(group) && hasActors(group, ComponentActor.class))
      actorError("Component targets cannot have subcomponent actors", SYS_COMPONENTS);

    if (1 < countActors(group, World.class))
      actorError("There can be at most one context actor", TOO_MANY_CONTEXTS);
  }

  private void actorError(String string, String code) {
    error(string, RoboCertPackage.Literals.SPECIFICATION_GROUP__ACTORS, code);
  }

  //
  // Utility functions
  //

  // TODO(@MattWindsor91): I think these are used/useful/duplicated elsewhere?

  private boolean hasComponentTarget(SpecificationGroup g) {
    return g.getTarget() instanceof ComponentTarget;
  }

  private boolean hasActors(SpecificationGroup g, Class<? extends Actor> clazz) {
    return g.getActors().parallelStream().anyMatch(clazz::isInstance);
  }

  private long countActors(SpecificationGroup g, Class<? extends Actor> clazz) {
    return g.getActors().parallelStream().filter(clazz::isInstance).count();
  }
}
