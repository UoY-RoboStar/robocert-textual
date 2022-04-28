/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.validation.seq;

import com.google.inject.Inject;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robocalc.robocert.model.robocert.DurationFragment;
import robocalc.robocert.model.robocert.RoboCertPackage.Literals;
import robocalc.robocert.model.robocert.World;

/**
 * Validates the well-formedness conditions on {@link DurationFragment} elements.
 *
 * @author Matt Windsor
 */
public class DurationFragmentValidator extends AbstractDeclarativeValidator {
  @Override
  @Inject
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  // TODO(@MattWindsor91): this is very similar to the LifelineOccurrence checks, and I think
  // the two should have a similar metamodel inheritance.

  /**
   * Checks that a duration fragment is not bound to a {@link World}.
   * @param fragment the duration fragment.
   */
  @Check
  public void checkNotWorld(DurationFragment fragment) {
    if (fragment.getActor() instanceof World) {
      error("Duration fragment cannot be bound to a World", Literals.DURATION_FRAGMENT__ACTOR, "SDA1");
    }
  }
}
