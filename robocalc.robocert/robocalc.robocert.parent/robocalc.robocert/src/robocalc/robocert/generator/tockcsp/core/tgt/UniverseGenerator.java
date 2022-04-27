/*******************************************************************************
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
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.generator.csp.comp.untimed.CGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.CollectionTarget;
import robocalc.robocert.model.robocert.ComponentTarget;
import robocalc.robocert.model.robocert.Target;

/**
 * Generates references to the universe sets for targets.
 *
 * @param csp low-level CSP generator.
 * @param gu  RoboChart generator utilities.
 */
public record UniverseGenerator(CSPStructureGenerator csp, CGeneratorUtils gu) {

  /**
   * Constructs a universe generator.
   *
   * @param csp low-level CSP generator.
   * @param gu  RoboChart generator utilities.
   */
  @Inject
  public UniverseGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
  }

  /**
   * Generates a reference to the semantic events set of the given target.
   *
   * <p>We currently use this as the universe set; technically it is an overapproximation as it
   * doesn't account for directionality of events.
   *
   * @param t the target for which we want the event set.
   * @return the CSP-M name of the semantic events set.
   */
  public CharSequence generate(Target t) {
    final var processes = processes(t);
    final var evts = processes.map(x -> csp.namespaced(gu.processId(x), "sem__events"))
        .toArray(CharSequence[]::new);
    return csp.union(evts);
  }

  private Stream<EObject> processes(Target t) {
    if (t instanceof ComponentTarget c) {
      return Stream.of(c.getElement());
    }
    if (t instanceof CollectionTarget c) {
      return c.getComponents().stream().map(x -> x);
    }
    throw new IllegalArgumentException("can't get processes of target %s".formatted(t));
  }
}
