/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import robostar.robocert.textual.generator.intf.core.SpecGroupField;
import robostar.robocert.textual.generator.intf.seq.context.InteractionContext;
import robostar.robocert.textual.generator.intf.seq.context.Synchronisation;
import robostar.robocert.textual.generator.tockcsp.core.group.SpecificationGroupElementFinder;
import robostar.robocert.textual.generator.tockcsp.core.tgt.TerminationGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.Interaction;
import robostar.robocert.InteractionFragment;
import robostar.robocert.util.StreamHelper;

/**
 * Generates synchronisation channel definitions for an interaction context.
 *
 * @param csp     CSP structure generator.
 * @param sgf     specification group element finder.
 * @param termGen used for generating references to the termination generator.
 */
public record SyncChannelGenerator(CSPStructureGenerator csp, SpecificationGroupElementFinder sgf,
                                   TerminationGenerator termGen) {

  /**
   * Constructs a sync channel generator.
   *
   * @param csp     CSP structure generator.
   * @param sgf     specification group element finder.
   * @param termGen used for generating references to the termination generator.
   */
  @Inject
  public SyncChannelGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(sgf);
    Objects.requireNonNull(termGen);
  }

  /**
   * Generates the synchronisation channel definitions for an interaction.
   *
   * <p>This includes the control channel set, which is always generated.
   *
   * @param ctx context for the interaction for which we are generating the channel.
   * @return the synchronisation channel definitions and control set definition.
   */
  public Stream<CharSequence> generate(InteractionContext ctx) {
    final var syncs = Stream.of(ctx.untils(), ctx.pars())
        .filter(x -> x.mustSynchronise(ctx.numLifelines())).toList();

    final var chanDefs = syncs.stream().flatMap(x -> channel(ctx, x).stream());

    final var chanNames = StreamHelper.push(termGen.terminateEvent(null).toString(),
        syncs.stream().map(Synchronisation::channelBase)).toArray(CharSequence[]::new);
    final var ctrlDef = csp.definition(ctrlSetName(ctx.seq()), csp.enumeratedSet(chanNames));

    return Stream.concat(chanDefs, Stream.of(ctrlDef));
  }

  private <T extends InteractionFragment> Optional<CharSequence> channel(InteractionContext ctx,
      Synchronisation<T> sync) {
    if (!sync.mustSynchronise(ctx.numLifelines())) {
      return Optional.empty();
    }

    // SyncDir is defined in the RoboCert standard library.
    return Optional.of(
        csp.channel(sync.channelBase(), "{0..%d}".formatted(sync.fragments().size() - 1),
            "SyncDir"));
  }

  //
  // Functions for naming sync channels
  //
  // TODO(@MattWindsor91): instead emit one module per sequence?
  //

  /**
   * Synthesises a suitable channel name for the control channel set for the given interaction.
   *
   * <p>This set should be put into the alphabet of each lifeline, and hidden at the top level
   * (before placing in parallel with the memory process).
   *
   * @param s the interaction in question.
   * @return an until channel name (not qualified).
   */
  public String ctrlSetName(Interaction s) {
    return "ctrl_" + s.getName();
  }

  /**
   * Lifts a channel name into a qualified form with respect to the current specification group's
   * channel module.
   *
   * @param base the base name.
   * @return a qualified form of the base name.
   */
  public CharSequence qualified(CharSequence base) {
    return csp.namespaced(SpecGroupField.CHANNEL_MODULE.toString(), base);
  }

  /**
   * Synthesises a suitable channel name for the until synchronisation for the given interaction.
   *
   * @param s the interaction in question.
   * @return an until channel name (not qualified).
   */
  public String untilChannelName(Interaction s) {
    return "until_" + s.getName();
  }

  /**
   * Synthesises a suitable channel name for the par synchronisation for the given interaction.
   *
   * @param s the interaction in question.
   * @return a par channel name (not qualified).
   */
  public String parChannelName(Interaction s) {
    return "par_" + s.getName();
  }


}
