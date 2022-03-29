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

package robocalc.robocert.generator.tockcsp.seq.message;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.NamedMessageSet;
import robocalc.robocert.model.robocert.SpecificationGroup;

/**
 * Generates the named sets module for a specification group.
 *
 * <p>This module enumerates all of the named message sets defined by the user on the group, as well
 * as auto-generated sets such as the target's universe.
 */
public record NamedSetModuleGenerator(CSPStructureGenerator csp, MessageSetGenerator setGenerator) {
  /**
   * Constructs a named set module generator.
   *
   * @param csp generator for CSP structures.
   * @param setGenerator generator for message sets.
   */
  @Inject
  public NamedSetModuleGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(setGenerator);
  }

  /**
   * Generates the named set module for a sequence group.
   *
   * @param group the group containing the sets.
   * @return generated CSP for the named message set group.
   */
  public CharSequence generate(SpecificationGroup group) {
    final var sets = group.getMessageSets();
    final var stdSets = Stream.of(universeDef(group));
    final var userSets = sets.stream().filter(Objects::nonNull)
        .map(x -> csp.definition(x.getName(), generateNamedSet(x)));
    final var allSets = Stream.concat(stdSets, userSets);

    return csp.module(MessageSetGenerator.MODULE_NAME, allSets.collect(Collectors.joining("\n")));
  }

  private CharSequence universeDef(SpecificationGroup group) {
    // TODO(@MattWindsor91): this seems overly broad.
    return csp.definition(MessageSetGenerator.UNIVERSE_NAME, semEvents(group));
  }

  private CharSequence semEvents(SpecificationGroup group) {
    return csp.namespaced(group.getTarget().getElement().getName(), "sem__events");
  }

  private CharSequence generateNamedSet(NamedMessageSet it) {
    return setGenerator.optimiseAndGenerate(it.getSet(), it::setSet);
  }
}
