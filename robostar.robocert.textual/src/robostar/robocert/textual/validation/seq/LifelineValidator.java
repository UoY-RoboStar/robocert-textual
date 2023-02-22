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
import robostar.robocert.Lifeline;
import robostar.robocert.RoboCertPackage.Literals;
import robostar.robocert.wfc.seq.LifelineChecker;

/**
 * Validates aspects of lifelines.
 *
 * @author Matt Windsor
 */
public class LifelineValidator extends AbstractDeclarativeValidator {

  @Inject
  private LifelineChecker checker;

  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  /**
   * Checks to see if the actor of a lifeline is present on the parent group (well-formedness
   * condition {@code SLA1}).
   *
   * @param line the lifeline to check
   */
  @Check
  public void checkSLA1(Lifeline line) {
    if (!checker.isSLA1(line)) {
      error("The actor of a lifeline must be present on the parent group.",
          Literals.LIFELINE__ACTOR, "SLA1");
    }
  }
}
