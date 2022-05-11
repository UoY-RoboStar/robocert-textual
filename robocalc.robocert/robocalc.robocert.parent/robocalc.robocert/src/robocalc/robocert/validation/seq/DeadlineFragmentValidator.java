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
import robostar.robocert.DeadlineFragment;
import robostar.robocert.RoboCertPackage.Literals;
import robostar.robocert.World;

/**
 * Validates the well-formedness conditions on {@link DeadlineFragment} elements.
 *
 * @author Matt Windsor
 */
public class DeadlineFragmentValidator extends AbstractDeclarativeValidator {
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
  public void checkNotWorld(DeadlineFragment fragment) {
    if (fragment.getActor() instanceof World) {
      error("Deadline fragment cannot be bound to a World", Literals.DEADLINE_FRAGMENT__ACTOR, "SDA1");
    }
  }
}
