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
 *   $author - initial definition
 ******************************************************************************/

package robostar.robocert.textual.generator.tockcsp.seq.fragment.until;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.message.MessageSetGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.message.MessageGenerator;
import robostar.robocert.textual.generator.utils.InitialSetBuilder;
import robostar.robocert.InteractionOperand;
import robostar.robocert.UntilFragment;

/**
 * Generates CSP-M for the header part of {@code UntilFragment}s.
 *
 * @author Matt Windsor
 */
public record UntilFragmentHeaderGenerator(CSPStructureGenerator csp,
                                           InitialSetBuilder initialSetBuilder,
                                           MessageSetGenerator messageSetGen,
                                           MessageGenerator messageGen) {

  @Inject
  public UntilFragmentHeaderGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(initialSetBuilder);
    Objects.requireNonNull(messageSetGen);
    Objects.requireNonNull(messageGen);
  }

  /**
   * Generates CSP-M for an until-lifting function.
   *
   * @param fragment the fragment being lifted.
   * @return the generated CSP-M.
   */
  public CharSequence generate(UntilFragment fragment) {
    return csp.function(UNTIL_PROC, intraMessageSet(fragment), initialSet(fragment.getBody()));
  }

  /**
   * Optimises the intra-message set in place, then generates it.
   * <p>
   * We do the optimisation like this to preserve containment information, so sequence group lookup
   * works.
   *
   * @param fragment the fragment.
   * @return the generated CSP.
   */
  private CharSequence intraMessageSet(UntilFragment fragment) {
    return messageSetGen.optimiseAndGenerate(fragment.getIntraMessages(),
        fragment::setIntraMessages);
  }

  /**
   * Generates the initial message set for an interaction operand.
   * <p>
   * This is to avoid the possibility of both the until-fragment and its enclosed subsequence
   * offering the same CSP events.
   *
   * @param op the operand for which we are generating CSP.
   * @return the generated CSP sequence.
   */
  private CharSequence initialSet(InteractionOperand op) {
    return messageGen.generateBulkCSPEventSet(initialSetBuilder.initialSet(op).toList());
  }

  /**
   * Name of the process that implements until fragments.
   */
  private static final String UNTIL_PROC = "Until"; // in robocert_seq_defs
}
