/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.eclipse.xtext.EcoreUtil2;
import robostar.robocert.textual.generator.intf.seq.context.ActorContext;
import robostar.robocert.textual.generator.intf.seq.context.InteractionContext;
import robostar.robocert.textual.generator.intf.seq.context.Synchronisation;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.InteractionFragment;
import robostar.robocert.ParFragment;
import robostar.robocert.UntilFragment;
import robostar.robocert.World;

/**
 * Creates, from a sequence, a series of lifeline contexts for use in generating individual
 * lifelines.
 *
 * @param csp      low-level CSP generator.
 * @param actorGen used to get data constructor names for actors.
 * @param syncGen  used to work out whether we need an until-process and, if so, which fragments
 *                 will go into it.
 * @author Matt Windsor
 */
public record LifelineContextFactory(CSPStructureGenerator csp, ActorGenerator actorGen,
                                     SyncChannelGenerator syncGen) {

  /**
   * Constructs a lifeline context factory.
   *
   * @param csp      low-level CSP generator.
   * @param actorGen used to get data constructor names for actors.
   * @param syncGen  used to get names for synchronisation channels.
   */
  @Inject
  public LifelineContextFactory {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(actorGen);
    Objects.requireNonNull(syncGen);
  }

  /**
   * Creates an interaction context.
   *
   * @param s the sequence for which we are creating contexts.
   * @return the interaction context.
   */
  public InteractionContext context(Interaction s) {
    final var visibleActors = s.getActors().stream().filter(this::actorVisibleInSemantics).toList();

    final var untils = makeSynchronisation(s, syncGen::untilChannelName, UntilFragment.class);
    final var pars = makeSynchronisation(s, syncGen::parChannelName, ParFragment.class);

    return new InteractionContext(s, visibleActors, untils, pars);
  }

  private <T extends InteractionFragment> Synchronisation<T> makeSynchronisation(Interaction s,
      Function<Interaction, CharSequence> makeName, Class<T> clazz) {
    final var channelBase = makeName.apply(s);
    // TODO(@MattWindsor91): make carrying this in the Synchronisation redundant?
    final var channel = syncGen.qualified(channelBase);
    return new Synchronisation<>(EcoreUtil2.eAllOfType(s, clazz), channel, channelBase);
  }

  /**
   * Creates contexts for each semantics-visible lifeline in the given interaction context.
   *
   * <p>Not all lifelines are visible; any that form a context do not appear in the semantics as
   * they are considered to be the CSP environment.
   *
   * @param ctx the parent interaction context.
   * @return the list of actor contexts.
   */
  public List<ActorContext> actors(InteractionContext ctx) {
    return ctx.lifelines().parallelStream()
        .map(a -> new ActorContext(ctx, a, actorGen.dataConstructor(a))).toList();
  }

  private boolean actorVisibleInSemantics(Actor a) {
    // This may change in future.
    return !(a instanceof World);
  }
}
