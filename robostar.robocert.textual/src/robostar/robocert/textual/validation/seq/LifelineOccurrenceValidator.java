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

package robostar.robocert.textual.validation.seq;

import com.google.inject.Inject;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robostar.robocert.LifelineOccurrence;
import robostar.robocert.RoboCertPackage.Literals;
import robostar.robocert.World;

/**
 * Validates the well-formedness conditions on {@link LifelineOccurrence} elements.
 *
 * @author Matt Windsor
 */
public class LifelineOccurrenceValidator extends AbstractDeclarativeValidator {
  @Override
  @Inject
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  // TODO(@MattWindsor91): this is very similar to the DurationFragment checks, and I think
  // the two should have a similar metamodel inheritance.

  /**
   * Checks that a lifeline occurrence is not bound to a {@link World}.
   * @param occ the occurrence.
   */
  @Check
  public void checkNotWorld(LifelineOccurrence occ) {
    if (occ.getActor() instanceof World) {
      error("Occurrence cannot be bound to a World", Literals.LIFELINE_OCCURRENCE__ACTOR, "SLoA1");
    }
  }
}
