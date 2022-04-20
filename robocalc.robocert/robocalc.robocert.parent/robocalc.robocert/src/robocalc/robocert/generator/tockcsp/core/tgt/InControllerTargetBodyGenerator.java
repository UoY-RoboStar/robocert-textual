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
 *   Alvaro Miyazawa and Pedro Ribeiro - initial definition in RoboChart
 *   Matt Windsor - port to RoboCert
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedControllerGenerator;
import com.google.inject.Inject;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.InControllerTarget;

/**
 * Generates bodies of in-controller targets.
 *
 * @author Matt Windsor
 */
public class InControllerTargetBodyGenerator extends
    CollectionTargetBodyGenerator<InControllerTarget, ControllerDef, ControllerDef> {

  @Inject
  protected CTimedControllerGenerator ctrlGen;

  @Override
  protected ControllerDef element(InControllerTarget target) {
    return target.getController();
  }

  @Override
  protected String namespace(ControllerDef element) {
    return gu.ctrlName(element);
  }

  @Override
  protected ControllerDef context(ControllerDef element) {
    return element;
  }

  @Override
  protected Stream<CharSequence> componentVars(ControllerDef element) {
    return element.getMachines().stream()
        .flatMap(s -> gu.requiredVariables(gu.stmDef(s)).stream().map(v -> csp.namespaced(gu.stmName(s), extSet(v))));
  }

  @Override
  protected CharSequence innerBody(String ns, ControllerDef element, ControllerDef ctx) {
    return ctrlGen.composeStateMachines(element, element.getMachines(), element.getConnections(),
        false, false);
  }

  @Override
  protected CharSequence wrapOuter(ControllerDef element, ControllerDef ctx, CharSequence body) {
    return body;
  }
}
