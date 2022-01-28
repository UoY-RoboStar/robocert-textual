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
import robocalc.robocert.model.robocert.BlockStep;
import robocalc.robocert.model.robocert.BranchStep;
import robocalc.robocert.model.robocert.MessageOccurrence;
import robocalc.robocert.model.robocert.InteractionFragment;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.OccurrenceFragment;
import robocalc.robocert.model.robocert.Occurrence;
import robocalc.robocert.model.robocert.Subsequence;
import robocalc.robocert.model.robocert.UntilFragment;

/**
 * Handles the construction of initial message sets for until-fragments.
 */
public class InitialSetBuilder {
  /**
   * Gets the initial message set for a subsequence, as a stream of message sets to be united.
   *
   * This is empty if the subsequence is empty or an occurrence other than a message, the singleton
   * set containing the message if it is a message occurrence, and will currently throw in any
   * other situation.  More scenarios will be specified in future.
   *
   * @param sseq the subsequence.
   * @return the messages that the subsequence can initially offer.
   */
  public Stream<Message> initialSet(Subsequence sseq) {
    return sseq.getFragments().stream().limit(1).flatMap(this::fragmentInitialSet).distinct();
  }

  private Stream<Message> fragmentInitialSet(InteractionFragment fragment) {
    if (fragment instanceof OccurrenceFragment o)
      return occurrenceInitialSet(o.getOccurrence());

    // This should be caught by well-formedness, but we'll defend against it here too.
    //
    // (The problem is that we can't enumerate the initial set of an UntilFragment, because it
    // could be the universe.  We might be able to relax this restriction eventually by making
    // the InitialSetBuilder return an optimised MessageSet, but this might be tricky to make work
    // as we don't have a parent SequenceGroup ready.)
    if (fragment instanceof UntilFragment)
      throw new IllegalArgumentException("can't (yet) nest UntilFragments");

    // Recurse down the subsequences of combined fragments, assuming any well-formedness issues
    // have already been dealt with.
    if (fragment instanceof BlockStep b)
      return initialSet(b.getBody());
    if (fragment instanceof BranchStep b)
      return branchInitialSet(b);

    throw new IllegalArgumentException("can't get initial set for fragment %s".formatted(fragment));
  }

  private Stream<Message> branchInitialSet(BranchStep b) {
    return b.getBranches().parallelStream().flatMap((x) -> initialSet(x.getBody()));
  }

  private Stream<Message> occurrenceInitialSet(Occurrence occ) {
    if (occ instanceof MessageOccurrence m)
      return Stream.of(m.getMessage());

    return Stream.empty();
  }
}
