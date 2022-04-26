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

package robocalc.robocert.generator.tockcsp.seq.fragment.until;

import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.UntilFragment;
import robocalc.robocert.model.robocert.World;

/**
 * Generates the various aspects of an {@link UntilFragment} construct within an interaction.
 *
 * <p>The semantics of an {@link UntilFragment} on an interaction with multiple lifelines is to
 * suspend all actions on all lifelines, synchronising those lifelines, and instead enable the body
 * of the fragment inside a separate process.  (If there is one lifeline, we instead just inline the
 * bodies of the fragments, to avoid generating extraneous CSP.)
 *
 * <p>Fragments are referred-to by occurrence order; this means that the list of fragments must
 * be kept in the same order across all calls within this generator.
 *
 * @param csp the low-level CSP structure generator.
 */
public record UntilFragmentProcessGenerator(CSPStructureGenerator csp) {

  /**
   * Constructs an until-fragment process generator.
   *
   * @param csp the low-level CSP structure generator.
   */
  @Inject
  public UntilFragmentProcessGenerator {
    Objects.requireNonNull(csp);
  }

  /**
   * Gets all until fragments in a sequence that need to be put into a process.
   *
   * <p>This will be empty if there are no fragments, or if there are not enough lifelines in the
   * interaction to warrant a separate process.
   *
   * <p>This should be passed verbatim to each method accepting a list of until fragments, as the
   * semantics is sensitive to the position of the fragments within the list.
   *
   * @param seq the sequence to inspect.
   * @return the list of fragments within the sequence.
   */
  public List<UntilFragment> processFragments(Interaction seq) {
    // If there is only one non-World actor, we don't need a process.
    // TODO(@MattWindsor91): this duplicates InteractionContext.
    if (seq.getActors().stream().filter(x -> !(x instanceof World)).count() < 2) {
      return List.of();
    }

    return EcoreUtil2.eAllOfType(seq, UntilFragment.class);
  }

  /**
   * Generates the until-process channel definition for a sequence.
   *
   * @param seq the sequence for which we are generating the channel.
   * @return the until synchronisation channel definition (empty if the sequence doesn't need one).
   */
  public Optional<CharSequence> generateChannel(Interaction seq) {
    // TODO(@MattWindsor91): repeatedly generating this fragment list (since we do it here, and in
    // context generation, and so on) might be slow.
    final var untils = processFragments(seq);

    if (untils.isEmpty()) {
      return Optional.empty();
    }

    // UntilSync is defined in the RoboCert standard library.
    return Optional.of(
        "channel %s : {0..%d}.UntilSyncDir".formatted(channelName(seq), untils.size() - 1));
  }

  /**
   * Gets the name of the channel used for synchronising the until-process.
   *
   * @param seq the sequence whose channel is to be named.
   * @return the name of the synchronising channel for {@code seq}.
   */
  public String channelName(Interaction seq) {
    return "until_" + seq.getName();
  }
}
