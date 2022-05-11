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

package robostar.robocert.textual.generator.intf.seq.fragment;

import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;
import robostar.robocert.BlockFragment;
import robostar.robocert.UntilFragment;

/**
 * Top-level generator for {@link BlockFragment}s.
 * <p>
 * The CSP-M compilation strategy for most block fragments is to lower them to the form F(P), where
 * P is the subsequence for the block and F is a function (possibly partially applied) representing
 * the 'header' of the fragment.
 * <p>
 * We make an exception for {@link UntilFragment}s in a multi-visible lifeline context.  For these,
 * we instead emit a process that stalls the lifeline until the separate until-process can perform
 * the body of the fragment.
 */
public interface BlockFragmentGenerator {

  /**
   * Generates CSP-M for a block fragment.
   *
   * @param fragment the fragment to generate.
   * @param ctx      the lifeline context for the current lifeline.
   * @return generated CSP-M for the block fragment.
   */
  CharSequence generate(BlockFragment fragment, LifelineContext ctx);
}
