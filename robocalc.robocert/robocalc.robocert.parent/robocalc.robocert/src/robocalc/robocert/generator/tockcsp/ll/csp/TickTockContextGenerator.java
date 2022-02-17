/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/
package robocalc.robocert.generator.tockcsp.ll.csp;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.core.TargetGenerator;
import robocalc.robocert.model.robocert.Target;

/**
 * Generates the appropriate tick-tock 'context' (minimal covering set of all events in a process)
 * for use in model-shifting tick-tock refinement to traces refinement.
 *
 * <p>See: J. Baxter, P. Ribeiro, A. Cavalcanti: Sound reasoning in tock-CSP. Acta Informatica.
 * (2021).
 *
 * @author Matt Windsor
 */
public class TickTockContextGenerator {
  @Inject private CSPStructureGenerator csp;
  @Inject private TargetGenerator tg;

  /**
   * Lifts the given process body into the tick-tock context of a target.
   *
   * @param t the target to use for lifting.
   * @param inner the body to lift.
   * @return the lifted body.
   */
  public CharSequence liftTickTock(Target t, CharSequence inner) {
    // This should line up with how the RoboChart standard library implements model shifting.
    return csp.function(csp.namespaced(generateRef(t), "TT"), inner);
  }

  /**
   * Generates a reference to a tick-tock context for the given target.
   *
   * @param t the source of the tick-tock context.
   * @return CSP-M referring to the tick-tock context.
   */
  public CharSequence generateRef(Target t) {
    return tg.getFullCSPName(t, TargetField.TICK_TOCK_CONTEXT);
  }
}
