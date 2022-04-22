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

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.StateMachine;
import com.google.inject.Inject;
import java.util.List;

import circus.robocalc.robochart.ControllerDef;
import robocalc.robocert.model.robocert.util.resolve.ControllerResolver;

/**
 * Generates bodies of in-controller targets.
 *
 * @author Matt Windsor
 */
public class InControllerTargetBodyGenerator extends
    CollectionTargetBodyGenerator<ControllerDef, ControllerDef, StateMachine> {
  @Inject
  private ControllerResolver ctrlResolve;

  @Override
  protected String namespace(ControllerDef element) {
    return csp.namespaced(ctrlResolve.name(element)).toString();
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
  protected List<Connection> connections(ControllerDef element) {
    return element.getConnections();
  }

  @Override
  protected Context definition(StateMachine comp) {
    return gu.stmDef(comp);
  }

  @Override
  protected String name(StateMachine comp) {
    return gu.stmName(comp);
  }

  @Override
  protected Class<StateMachine> compClass() {
    return StateMachine.class;
  }

  @Override
  protected CharSequence wrapInner(ControllerDef element, ControllerDef ctx, CharSequence body) {
    return body;
  }

  @Override
  protected CharSequence wrapOuter(ControllerDef element, ControllerDef ctx, CharSequence body) {
    return body;
  }

}
