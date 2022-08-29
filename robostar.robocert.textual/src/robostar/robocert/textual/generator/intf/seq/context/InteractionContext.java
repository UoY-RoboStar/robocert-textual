/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.intf.seq.context;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.ParFragment;
import robostar.robocert.UntilFragment;

/**
 * Context required for generating an interaction.
 *
 * <p>This record answers a number of optimisation and semantic questions about the interaction,
 * such as how many lifelines (semantics-visible actors) are present, and whether we need to emit
 * certain parts of the full semantics or can get away with eliding them.
 *
 * <p>Each lifeline context contains a reference to this context.
 *
 * @param seq       the interaction in question.
 * @param lifelines all actors that are visible in the semantics.
 * @param untils    any UntilFragments found in the interaction that must be moved out of
 *                  lifelines.
 * @param pars      any ParFragments found in the interaction that must be synchronised.
 */
public record InteractionContext(Interaction seq,
                                 List<Actor> lifelines,
                                 Synchronisation<UntilFragment> untils,
                                 Synchronisation<ParFragment> pars) {

  public InteractionContext {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(lifelines);
    Objects.requireNonNull(untils);
    Objects.requireNonNull(pars);
  }


  /**
   * Gets the number of lifelines (visible actors) in the interaction.
   *
   * @return the number of lifelines.
   */
  public int numLifelines() {
    return lifelines.size();
  }

  /**
   * Gets the until channel of this process, but only if it will actually be generated.
   *
   * @return the until channel, but only if needed; empty otherwise.
   */
  public Optional<CharSequence> untilChannelIfNeeded() {
    return untils.channelIfNeeded(numLifelines());
  }

  /**
   * Gets the parallel sync channel of this process, but only if it will actually be generated.
   *
   * @return the par channel, but only if needed; empty otherwise.
   */
  public Optional<CharSequence> parChannelIfNeeded() {
    return pars.channelIfNeeded(numLifelines());
  }
  
  /**
   * Gets the index of the given fragment in the until synchronisation, if any.
   * 
   * <p>This differs from checking the list directly in that, if synchronisation is disabled,
   * we return -1.
   * 
   * @param fragment until fragment to query.
   * @return -1 if the fragment does not need synchronising; its index, otherwise.
   */
  public int untilIndex(UntilFragment frag) {
    return untils.mustSynchronise(numLifelines()) ? untils.fragments().indexOf(frag) : -1;
  }
}
