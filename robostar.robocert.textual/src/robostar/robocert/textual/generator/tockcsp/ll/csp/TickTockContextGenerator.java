/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.ll.csp;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.textual.generator.intf.core.SpecGroupField;
import robostar.robocert.textual.generator.tockcsp.core.group.SpecificationGroupElementFinder;
import robostar.robocert.SpecificationGroup;

/**
 * Generates the appropriate tick-tock 'context' (minimal covering set of all events in a process)
 * for use in model-shifting tick-tock refinement to traces refinement.
 *
 * <p>See: J. Baxter, P. Ribeiro, A. Cavalcanti: Sound reasoning in tock-CSP. Acta Informatica.
 * (2021).
 *
 * @author Matt Windsor
 */
public record TickTockContextGenerator(CSPStructureGenerator csp, SpecificationGroupElementFinder elementFinder) {
  @Inject public TickTockContextGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(elementFinder);
  }

  /**
   * Lifts the given process body into the tick-tock context of a target.
   *
   * @param group the group whose target is wanted.
   * @param inner the body to lift.
   * @return the lifted body.
   */
  public CharSequence liftTickTock(SpecificationGroup group, CharSequence inner) {
    // This should line up with how the RoboChart standard library implements model shifting.
    return csp.function(csp.namespaced(generateRef(group), "TT"), inner);
  }

  /**
   * Generates a reference to a tick-tock context for the target of the given specification group.
   *
   * @param group the group whose target is wanted.
   * @return CSP-M referring to the tick-tock context.
   */
  public CharSequence generateRef(SpecificationGroup group) {
    return elementFinder.getFullCSPName(group, SpecGroupField.TICK_TOCK_CONTEXT);
  }
}
