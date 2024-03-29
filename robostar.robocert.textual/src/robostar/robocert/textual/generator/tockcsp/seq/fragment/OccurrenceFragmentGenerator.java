/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;

import java.util.stream.Stream;
import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;
import robostar.robocert.textual.generator.intf.seq.OccurrenceGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.Actor;
import robostar.robocert.LifelineOccurrence;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.Occurrence;
import robostar.robocert.OccurrenceFragment;

/**
 * Generates CSP-M for action steps.
 *
 * @author Matt Windsor
 */
public record OccurrenceFragmentGenerator(CSPStructureGenerator csp, OccurrenceGenerator occGen) {
  // This generator handles the injection of loads for any possible
  // expressions in the action, as it is safe to do so at this level (no
  // Action recursively includes any more Steps or Actions).
  //
  // It does *not* handle the injection of stores; we do that in the
  // generator for MessageOccurrences.

  /**
   * Constructs an action step generator.
   *
   * @param csp    CSP structure generator.
   * @param occGen occurrence generator.
   */
  @Inject
  public OccurrenceFragmentGenerator {
  }

  /**
   * Generates CSP-M for an occurrence fragment, from the perspective of a particular lifeline.
   *
   * @param fragment the occurrence fragment.
   * @param ctx      context for the current lifeline.
   * @return the generated CSP-M.
   */
  public CharSequence generate(OccurrenceFragment fragment, LifelineContext ctx) {
    final var occ = fragment.getOccurrence();

    // The occurrence must be relevant to this lifeline to generate it at all.
    if (!ctx.isForAnyOf(occurrenceActors(occ))) {
      return csp.commented("occurrence on %s".formatted(ctx.actorName()), csp.skip());
    }

    return occGen.generate(occ);
  }

  private Stream<Actor> occurrenceActors(Occurrence occ) {
    if (occ instanceof LifelineOccurrence l) {
      return Stream.of(l.getActor());
    }
    if (occ instanceof MessageOccurrence m) {
      return Stream.of(m.getMessage().getFrom(), m.getMessage().getTo());
    }
    throw new IllegalArgumentException("unsupported occurrence: %s".formatted(occ));
  }
}
