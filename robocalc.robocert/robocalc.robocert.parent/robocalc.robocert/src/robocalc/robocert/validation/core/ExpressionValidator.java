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

package robocalc.robocert.validation.core;

import circus.robocalc.robochart.FloatExp;
import circus.robocalc.robochart.RoboChartPackage.Literals;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

/**
 * Validates certain aspects of expressions.
 *
 * @author Matt Windsor
 */
public class ExpressionValidator extends AbstractDeclarativeValidator {
  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  @Check
  public void checkNoFloats(FloatExp f) {
    warning("Floating-point expressions are not supported yet in the CSP generator", Literals.FLOAT_EXP__VALUE);
  }
}
