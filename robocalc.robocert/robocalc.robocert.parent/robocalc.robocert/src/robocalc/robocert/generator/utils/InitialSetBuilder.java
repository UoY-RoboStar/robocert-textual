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

package robocalc.robocert.generator.utils;

import java.util.stream.Stream;
import robocalc.robocert.model.robocert.MessageOccurrence;
import robocalc.robocert.model.robocert.InteractionFragment;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.OccurrenceFragment;
import robocalc.robocert.model.robocert.Occurrence;
import robocalc.robocert.model.robocert.Subsequence;

/**
 * Handles the construction of initial message sets for until-fragments.
 */
public class InitialSetBuilder {
  /**
   * Gets the initial message set for a subsequence, as a stream.
   *
   * This is empty if the subsequence is empty or an occurrence other than a message, the singleton
   * set containing the message if it is a message occurrence, and will currently throw in any
   * other situation.  More scenarios will be specified in future.
   *
   * @param sseq the subsequence.
   * @return the messages that the subsequence can initially offer.
   */
  public Stream<MessageSpec> initialSet(Subsequence sseq) {
    return sseq.getFragments().stream().limit(1).flatMap(this::fragmentInitialSet).distinct();
  }

  private Stream<MessageSpec> fragmentInitialSet(InteractionFragment fragment) {
    // TODO(@MattWindsor91): expand this to consider more.
    if (fragment instanceof OccurrenceFragment o)
      return occurrenceInitialSet(o.getOccurrence());

    throw new IllegalArgumentException("can't get initial set for fragment %s".formatted(fragment));
  }

  private Stream<MessageSpec> occurrenceInitialSet(Occurrence occ) {
    if (occ instanceof MessageOccurrence a)
      return Stream.of(a.getBody());

    return Stream.empty();
  }
}
