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
import java.util.Objects;
import robocalc.robocert.generator.intf.seq.context.InteractionContext;
import robocalc.robocert.generator.intf.seq.context.UntilContext;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.intf.seq.fragment.BlockFragmentGenerator;
import robocalc.robocert.generator.tockcsp.seq.SyncChannelGenerator;
import robocalc.robocert.model.robocert.UntilFragment;

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
public record UntilFragmentProcessGenerator(CSPStructureGenerator csp,
                                            BlockFragmentGenerator blockGen) {

  /**
   * Constructs an until-fragment process generator.
   *
   * @param csp      the low-level CSP structure generator.
   * @param blockGen the block generator, used for expanding the individual until-fragments.
   */
  @Inject
  public UntilFragmentProcessGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(blockGen);
  }

  /**
   * Constructs an until-process.
   *
   * @param ctx the context for the interaction whose process we are producing.
   * @return the CSP-M definition for the process.
   */
  public CharSequence process(InteractionContext ctx) {
    final var ictx = new UntilContext(ctx);
    final var cs = csp.sets();

    var body = cs.tuple(csp.pre(SyncChannelGenerator.TERM_CHANNEL, csp.skip()));

    final var chanBase = ctx.untils().channel();
    final var fragments = ctx.untils().fragments();
    for (var i = 0; i < fragments.size(); i++) {
      final var chan = "%s.%d".formatted(chanBase, i);

      final var fragBody = blockGen.generate(fragments.get(i), ictx);
      // These should match the standard library definition of UntilSyncDir.
      final var enter = "%s.enter".formatted(chan);
      final var leave = "%s!leave".formatted(chan);
      final var withChans = csp.seq(csp.pre(enter, fragBody), csp.pre(leave, NAME));
      body = csp.bins().extChoice(body, cs.tuple(withChans));
    }

    return csp.definition(NAME, body);
  }

  /**
   * The name of the generated process.
   */
  public static final String NAME = "until";
}
