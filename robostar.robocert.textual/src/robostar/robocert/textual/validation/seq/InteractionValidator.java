/*
 * Copyright (c) 2021-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.validation.seq;

import com.google.inject.Inject;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robostar.robocert.Interaction;
import robostar.robocert.RoboCertPackage.Literals;
import robostar.robocert.wfc.seq.InteractionChecker;

/**
 * Validates aspects of interactions.
 *
 * @author Matt Windsor
 */
public class InteractionValidator extends AbstractDeclarativeValidator {

  @Inject
  private InteractionChecker checker;

  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  /**
   * Checks to see if the lifelines of an interaction reference each of the actors of its parent
   * group exactly once (well-formedness condition {@code SIL1}).
   *
   * @param seq the interaction to check
   */
  @Check
  public void checkSIL1(Interaction seq) {
    if (!checker.isSIL1(seq)) {
      error(
          "The lifelines of an interaction must reference each of the actors of its parent group exactly once.",
          Literals.INTERACTION__LIFELINES, "SIL1");
    }
  }
}
