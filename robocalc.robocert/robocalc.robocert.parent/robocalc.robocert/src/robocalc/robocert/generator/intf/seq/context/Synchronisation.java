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

package robocalc.robocert.generator.intf.seq.context;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import robocalc.robocert.model.robocert.InteractionFragment;

/**
 * Records information about a list of fragments in an interaction that must be synchronised with a
 * channel (if there is at least one such fragment, and at least two lifelines).
 *
 * @param fragments list of relevant fragments found in the interaction.
 * @param channel   name of the synchronisation channel.
 * @param <T>       type of fragments being synchronised by this context.
 */
public record Synchronisation<T extends InteractionFragment>(List<T> fragments,
                                                             CharSequence channel) {

  /**
   * Constructs a fragment synchronisation record.
   *
   * @param fragments list of relevant fragments found in the interaction.
   * @param channel   name of the synchronisation channel.
   */
  public Synchronisation {
    Objects.requireNonNull(fragments);
    Objects.requireNonNull(channel);
    if (channel.isEmpty()) {
      throw new IllegalArgumentException("channel name must not be empty");
    }
  }

  /**
   * Gets whether the channel (and process, if needed) should be emitted for this synchronisation.
   *
   * @param numLifelines number of lifelines (visible actors) in the interaction.
   * @return true if there is more than one actor and more than zero elements.
   */
  public boolean mustSynchronise(int numLifelines) {
    return 1 < numLifelines && !fragments.isEmpty();
  }

  /**
   * Gets the sync channel of this process, but only if it will actually be generated.
   *
   * @param numLifelines number of lifelines (visible actors) in the interaction.
   * @return the sync channel if {@code mustSynchronise()} is true; empty otherwise.
   */
  public Optional<CharSequence> channelIfNeeded(int numLifelines) {
    return mustSynchronise(numLifelines) ? Optional.of(channel) : Optional.empty();
  }
}
