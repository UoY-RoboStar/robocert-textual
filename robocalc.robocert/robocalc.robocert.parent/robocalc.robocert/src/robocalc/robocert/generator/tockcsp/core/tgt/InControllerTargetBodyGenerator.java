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

import circus.robocalc.robochart.StateMachine;
import circus.robocalc.robochart.StateMachineDef;
import java.util.List;
import java.util.stream.Stream;

import com.google.inject.Inject;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedControllerGenerator;

/**
 * Generates bodies of in-controller targets.
 *
 * @author Matt Windsor
 */
public class InControllerTargetBodyGenerator extends
    CollectionTargetBodyGenerator<ControllerDef, ControllerDef, StateMachine> {

  @Inject
  protected CTimedControllerGenerator ctrlGen;

  @Override
  protected String namespace(ControllerDef element) {
    return gu.ctrlName(element);
  }

  @Override
  protected ControllerDef context(ControllerDef element) {
    return element;
  }

  @Override
  protected List<StateMachine> components(ControllerDef element) {
    return element.getMachines();
  }

  @Override
  protected Stream<CharSequence> componentVars(StateMachine element) {
    return gu.requiredVariables(defResolve.resolve(element)).stream()
        .map(v -> csp.namespaced(gu.stmName(element), extSet(v)));
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
