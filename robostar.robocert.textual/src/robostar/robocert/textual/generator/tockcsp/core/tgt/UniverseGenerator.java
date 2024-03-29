/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.generator.csp.comp.untimed.CGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.Strings;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.CollectionTarget;
import robostar.robocert.ComponentTarget;
import robostar.robocert.Target;
import robostar.robocert.util.resolve.TargetComponentsResolver;
import robostar.robocert.util.resolve.TargetElementResolver;

/**
 * Generates references to the universe sets for targets.
 *
 * @param csp     low-level CSP generator.
 * @param gu      RoboChart generator utilities.
 * @param elemRes resolves elements of targets.
 * @param compRes resolves components of targets.
 */
public record UniverseGenerator(CSPStructureGenerator csp, CGeneratorUtils gu,
                                TargetElementResolver elemRes, TargetComponentsResolver compRes) {

  /**
   * Constructs a universe generator.
   *
   * @param csp     low-level CSP generator.
   * @param gu      RoboChart generator utilities.
   * @param elemRes resolves elements of targets.
   * @param compRes resolves components of targets.
   */
  @Inject
  public UniverseGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
    Objects.requireNonNull(elemRes);
    Objects.requireNonNull(compRes);
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
    final var evts = processes.map(x -> csp.namespaced(processName(x), "sem__events"))
        .toArray(CharSequence[]::new);
    return csp.union(evts);
  }

  private CharSequence processName(EObject e) {
    return (e instanceof OperationRef r) ? substituteOperationDefName(r) : gu.processId(e);
  }

  /**
   * This handles the fact that the RoboChart generator inlines operation definitions within the
   * CSP for their previous controller.  The generated name given by processId has the right
   * container for the operation, we just need to replace the controller name with that of its
   * definition.
   *
   * <p> Related to GitHub issue #136.
   *
   * @param r operation reference.
   * @return the qualified CSP name of r but with the definition's name substituted at the end.
   */
  private CharSequence substituteOperationDefName(OperationRef r) {
    // TODO(@MattWindsor91): find a more elegant way of doing this?
    // TODO(@MattWindsor91): does anything else need this translation?
    final var elements = Strings.split(gu.processId(r), "::");
    elements.set(elements.size() - 1, "OP_" + r.getRef().getName());
    return csp.namespaced(elements.toArray(String[]::new));
  }

  private Stream<EObject> processes(Target t) {
    if (t instanceof ComponentTarget c) {
      return Stream.of(elemRes.resolve(c));
    }
    if (t instanceof CollectionTarget c) {
      // This map is needed to upcast to EObject.
      return compRes.resolve(c).map(x -> x);
    }
    throw new IllegalArgumentException("can't get processes of target %s".formatted(t));
  }
}
