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

package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.Actor;
import robostar.robocert.World;

/**
 * Generates various auxiliary definitions related to actors.
 *
 * @param csp helper for generating CSP structure.
 * @author Matt Windsor
 */
public record ActorGenerator(CSPStructureGenerator csp) {

  /**
   * Constructs an actor generator.
   *
   * @param csp helper for generating CSP structure.
   */
  @Inject
  public ActorGenerator {
    Objects.requireNonNull(csp);
  }

  /**
   * Generates the enumerating datatype for an actor.
   * <p>
   * This is used to index various other definitions, such as synchronisation sets and sequence
   * lifelines.
   *
   * @param name   the name of the datatype.
   * @param actors the list of actors for which we are generating the enumeration.  There must be at
   *               least one actor which is not a World.
   * @return CSP-M creating the enumeration, if one is necessary.
   */
  public Optional<CharSequence> generateType(CharSequence name, EList<Actor> actors) {
    final var names = actors.stream().filter(x -> !(x instanceof World)).map(this::dataConstructor)
        .toArray(CharSequence[]::new);
    return names.length == 0 ? Optional.empty() : Optional.of(csp.datatype(name, names));
  }

  /**
   * Generates a name for the data constructor of an actor.
   *
   * <p>This comes with a prefix to try disambiguate actor names from other things in the model,
   * as actor names tend to be very short and likely to clash.
   *
   * @param a the actor for which we are generating a data constructor name.
   * @return the data constructor name for the actor.
   */
  public CharSequence dataConstructor(Actor a) {
    return "Actor__%s".formatted(a.getName());
  }
}
