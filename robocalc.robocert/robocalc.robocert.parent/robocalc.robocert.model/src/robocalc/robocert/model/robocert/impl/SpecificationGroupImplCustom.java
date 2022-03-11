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
package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Variable;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.util.InstantiationHelper;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * Adds derived operation definitions to {@link SpecificationGroupImpl}.
 *
 * @author Matt Windsor
 */
public class SpecificationGroupImplCustom extends SpecificationGroupImpl {
  @Override
  public Expression getConstant(Variable v) {
    // TODO(@MattWindsor91): is this needed?
    return new InstantiationHelper().getConstant(assignments, v).orElse(null);
  }
	
  @Override
  public TargetActor basicGetTargetActor() {
    return getFirstActor(TargetActor.class);
  }

  @Override
  public World basicGetWorld() {
    return getFirstActor(World.class);
  }

  private <T extends Actor> T getFirstActor(Class<T> clazz) {
    return StreamHelpers.filter(getActors().parallelStream(), clazz).findFirst().orElse(null);
  }
}
