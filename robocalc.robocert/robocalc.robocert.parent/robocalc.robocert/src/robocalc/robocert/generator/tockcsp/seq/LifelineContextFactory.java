/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.seq.ActorContext;
import robocalc.robocert.generator.intf.seq.InteractionContext;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.seq.fragment.until.UntilFragmentProcessGenerator;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.World;

/**
 * Creates, from a sequence, a series of lifeline contexts for use in generating individual
 * lifelines.
 *
 * @param csp      low-level CSP generator.
 * @param actorGen used to get data constructor names for actors.
 * @param untilGen used to work out whether we need an until-process and, if so, which fragments
 *                 will go into it.
 * @author Matt Windsor
 */
public record LifelineContextFactory(CSPStructureGenerator csp, ActorGenerator actorGen,
                                     UntilFragmentProcessGenerator untilGen) {

  /**
   * Constructs a lifeline context factory.
   *
   * @param csp      low-level CSP generator.
   * @param actorGen used to get data constructor names for actors.
   * @param untilGen used to work out whether we need an until-process and, if so, which fragments
   *                 will go into it.
   */
  @Inject
  public LifelineContextFactory {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(actorGen);
    Objects.requireNonNull(untilGen);
  }

  /**
   * Creates an interaction context.
   *
   * @param s the sequence for which we are creating contexts.
   * @return the interaction context.
   */
  public InteractionContext context(Interaction s) {
    final var visibleActors = s.getActors().stream().filter(this::actorVisibleInSemantics).toList();
    final var untils = untilGen.processFragments(s);
    final var untilChannelName = csp.namespaced(SpecGroupField.CHANNEL_MODULE.toString(),
        untilGen.channelName(s));
    return new InteractionContext(visibleActors, untils, untilChannelName);
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
    return ctx.visibleActors().parallelStream()
        .map(a -> new ActorContext(ctx, a, actorGen.dataConstructor(a))).toList();
  }

  private boolean actorVisibleInSemantics(Actor a) {
    // This may change in future.
    return !(a instanceof World);
  }
}
