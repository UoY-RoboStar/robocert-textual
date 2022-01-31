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

package robocalc.robocert.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.OptFragment;
import robocalc.robocert.model.robocert.Temperature;

/**
 * Generates CSP-M for the header part of {@code OptFragment}s.
 *
 * @author Matt Windsor
 */
public record OptFragmentHeaderGenerator(CSPStructureGenerator csp) {

  /**
   * Constructs an optional fragment header generator.
   * @param csp helper for constructing CSP-M.
   */
  @Inject
  public OptFragmentHeaderGenerator {}

  /**
   * Generates CSP-M for the header of an optional fragment.
   *
   * @param fragment the fragment to generate.
   * @return CSP-M for the header.
   */
  public CharSequence generate(OptFragment fragment) {
    return fragment.getTemperature() == Temperature.COLD ? COLD_PROC : HOT_PROC;
  }

  // The following should be changed if the names change in robocert_seq_defs, and vice versa:

  /**
   * Process in robocert_seq_defs that defines a 'cold' (internal-choice) optionality.
   */
  private static final String COLD_PROC = "OptCold";

  /**
   * Process in robocert_seq_defs that defines a 'hot' (external-choice) optionality.
   */
  private static final String HOT_PROC = "OptHot";
}
