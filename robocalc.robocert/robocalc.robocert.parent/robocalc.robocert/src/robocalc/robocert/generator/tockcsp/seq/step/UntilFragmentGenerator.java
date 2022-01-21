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

package robocalc.robocert.generator.tockcsp.seq.step;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSpecGenerator;
import robocalc.robocert.generator.utils.InitialSetBuilder;
import robocalc.robocert.model.robocert.Subsequence;
import robocalc.robocert.model.robocert.UntilFragment;

/**
 * Generates CSP-M for {@code UntilFragment}s.
 */
public record UntilFragmentGenerator(
    CSPStructureGenerator csp,
    InitialSetBuilder initialSetBuilder,
    MessageSetGenerator messageSetGen,
    MessageSpecGenerator messageSpecGen,
    SubsequenceGenerator subsequenceGen)
 {

  @Inject
  public UntilFragmentGenerator {
  }

  /**
   * Generates CSP-M for a fragment.
   *
   * @param fragment the fragment to generate.
   * @param ctx the lifeline context for generation.
   * @return the generated CSP-M for the fragment.
   */
  public CharSequence generate(UntilFragment fragment, LifelineContext ctx) {
    final var body = subsequenceGen.generate(fragment.getBody(), ctx);
		return csp.function(untilFunction(fragment), body);
}

  /**
   * Generates CSP-M for an until-lifting function.
   *
   * @param fragment the fragment being lifted.
   * @return the generated CSP-M.
   */
  private CharSequence untilFunction(UntilFragment fragment) {
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
    return messageSetGen.optimiseAndGenerate(fragment.getIntraMessages(), fragment::setIntraMessages);
  }

  /**
   * Generates the initial message set for a subsequence.
   * <p>
   * This is to avoid the possibility of both the until-fragment and its enclosed subsequence
   * offering the same CSP events.
   *
   * @param sseq the action for which we are generating CSP.
   * @return the generated CSP sequence.
   */
  private CharSequence initialSet(Subsequence sseq) {
    return messageSpecGen.generateBulkCSPEventSet(initialSetBuilder.initialSet(sseq).toList());
  }

  /**
   * Name of the process that implements until fragments.
   */
  private static final String UNTIL_PROC = "Until"; // in robocert_seq_defs
}
