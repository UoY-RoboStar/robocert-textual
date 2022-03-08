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

package robocalc.robocert.generator.tockcsp.ll.csp;

import java.util.Objects;
import robocalc.robocert.model.robocert.SemanticModel;
import robocalc.robocert.model.robocert.SpecificationGroup;

/**
 * Holds information about a CSP refinement.
 *
 * Use this as an intermediate representation when lowering high-level properties into CSP-M
 * properties.
 *
 * @author Matt Windsor
 */
public record CSPRefinement(boolean isNegated, SpecificationGroup group, CharSequence lhs, CharSequence rhs, SemanticModel model) {
  public CSPRefinement {
    Objects.requireNonNull(group);
    Objects.requireNonNull(lhs);
    Objects.requireNonNull(rhs);
    Objects.requireNonNull(model);

    // context may be null
  }

  /**
   * Converts this refinement to CSP.
   * @param tt generator for tick-tock contexts.
   * @param csp generator for CSP structure.
   * @return the generated CSP refinement assertion.
   */
  public CharSequence toCSP(TickTockContextGenerator tt, CSPStructureGenerator csp) {
    return csp.assertion(isNegated, liftTauPriority(csp.refine(
        maybeLift(tt, lhs),
        maybeLift(tt, rhs),
        "T" // this may change if we ever support eg. failures or failures-divergences
    )));
  }

  /**
   * Possibly lifts a process into a tick-tock context.
   * @param tt the tick-tock context generator.
   * @param process the process to lift (if this refinement is under a tick-tock model).
   * @return the possibly-lifted process.
   */
  private CharSequence maybeLift(TickTockContextGenerator tt, CharSequence process) {
    return isTickTock() ? tt.liftTickTock(group, process) : process;
  }

  /**
   * Inserts a prioritisation if this refinement is NOT tick-tock.
   * @param process the inner process.
   * @return inner, wrapped if necessary in a tau/tock prioritisation.
   */
  private CharSequence liftTauPriority(CharSequence process) {
    return isTickTock() ? process : "%s :[tau priority]: {tock}".formatted(process);
  }

  private boolean isTickTock() {
    return model == SemanticModel.TIMED;
  }
}
