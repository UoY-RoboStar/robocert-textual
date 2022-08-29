/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.seq.message;

import circus.robocalc.robochart.generator.csp.comp.untimed.CGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.SpecificationGroup;

/**
 * Generates the named sets module for a specification group.
 *
 * <p>This module enumerates all the named message sets defined by the user on the group, as well
 * as auto-generated sets such as the target's universe.
 */
public record NamedSetModuleGenerator(CGeneratorUtils gu, CSPStructureGenerator csp,
                                      MessageSetGenerator setGenerator) {

  /**
   * Constructs a named set module generator.
   *
   * @param gu           RoboChart generator utilities.
   * @param csp          generator for CSP structures.
   * @param setGenerator generator for message sets.
   */
  @Inject
  public NamedSetModuleGenerator {
    Objects.requireNonNull(gu);
    Objects.requireNonNull(csp);
    Objects.requireNonNull(setGenerator);
  }

  /**
   * Generates the named set module for a sequence group.
   *
   * @param group the group containing the sets.
   * @return generated CSP for the named message set group, if one should be generated.
   */
  public Optional<CharSequence> generate(SpecificationGroup group) {
    final var sets = group.getMessageSets();
    final var userSets = sets.stream().filter(Objects::nonNull).map(
            x -> csp.definition(x.getName(), setGenerator.optimiseAndGenerate(x.getSet(), x::setSet)))
        .toArray(CharSequence[]::new);

    if (userSets.length == 0) {
      return Optional.empty();
    }
    return Optional.of(csp.module(MessageSetGenerator.MODULE_NAME).withPublic(userSets).end());
  }

}
