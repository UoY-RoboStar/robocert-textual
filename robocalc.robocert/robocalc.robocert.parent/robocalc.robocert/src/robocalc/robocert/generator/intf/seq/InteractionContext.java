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

package robocalc.robocert.generator.intf.seq;

import java.util.List;
import java.util.Objects;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.UntilFragment;

/**
 * Context required for generating an interaction.
 *
 * <p>Each lifeline context contains a reference to this context.
 *
 * @param visibleActors    all actors that are visible in the semantics.
 * @param untils           any UntilFragments found in the interaction that must be moved out of
 *                         lifelines; if this is empty, UntilFragments are to be kept inline.
 * @param untilChannel the name of this interaction's until synchronisation channel name, if
 *                         any.
 */
public record InteractionContext(List<Actor> visibleActors, List<UntilFragment> untils,
                                 CharSequence untilChannel) {

  public InteractionContext {
    Objects.requireNonNull(visibleActors);
    Objects.requireNonNull(untils);
    Objects.requireNonNull(untilChannel);
  }

  /**
   * Gets the index of an until fragment.
   *
   * <p>In interactions where the until fragments need to be moved out of lifelines into a
   * linearised process, each fragment will appear in the context's fragment list, and its index in
   * that list identifies the fragment in any synchronisation primitives used to hand over control
   * flow to and from that process.
   *
   * @param frag the fragment whose index is required.
   * @return -1 if this fragment does not have an index; the index of the fragment otherwise.
   */
  public int untilIndex(UntilFragment frag) {
    return untils.indexOf(frag);
  }
}
