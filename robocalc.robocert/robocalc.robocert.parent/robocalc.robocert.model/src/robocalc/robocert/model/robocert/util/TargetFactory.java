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

package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;
import java.util.Objects;
import robocalc.robocert.model.robocert.ControllerTarget;
import robocalc.robocert.model.robocert.InControllerTarget;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTarget;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.StateMachineTarget;

/**
 * High-level factory for producing RoboCert targets.
 *
 * @param certFactory the RoboCert factory.
 */
public record TargetFactory(RoboCertFactory certFactory) {
  /**
   * Constructs a target factory.
   * @param certFactory the RoboCert factory.
   */
  @Inject
  public TargetFactory {
    Objects.requireNonNull(certFactory);
  }

  //
  // CollectionTarget
  //

  /**
   * Constructs an in-module target over the given module.
   * @param module the module in question.
   * @return a target targetting the components of the given module.
   */
  public InModuleTarget inModule(RCModule module) {
    final var result = certFactory.createInModuleTarget();
    result.setModule(module);
    return result;
  }

  /**
   * Constructs an in-controller target over the given controller.
   * @param ctrl the controller in question.
   * @return a target targetting the components of the given controller.
   */
  public InControllerTarget inController(ControllerDef ctrl) {
    final var result = certFactory.createInControllerTarget();
    result.setController(ctrl);
    return result;
  }

  //
  // ComponentTarget
  //

  /**
   * Constructs a module target over the given module.
   * @param module the module in question.
   * @return a target targetting the given module.
   */
  public ModuleTarget module(RCModule module) {
    final var result = certFactory.createModuleTarget();
    result.setModule(module);
    return result;
  }

  /**
   * Constructs a controller target over the given controller.
   * @param ctrl the controller in question.
   * @return a target targetting the given controller.
   */
  public ControllerTarget controller(ControllerDef ctrl) {
    final var result = certFactory.createControllerTarget();
    result.setController(ctrl);
    return result;
  }

  /**
   * Constructs a state-machine target over the given state machine.
   * @param stm the state machine in question.
   * @return a target targetting the given state machine.
   */
  public StateMachineTarget stateMachine(StateMachineDef stm) {
    final var result = certFactory.createStateMachineTarget();
    result.setStateMachine(stm);
    return result;
  }

  /**
   * Constructs an operation target over the given operation.
   * @param op the operation in question.
   * @return a target targetting the given operation.
   */
  public OperationTarget operation(OperationDef op) {
    final var result = certFactory.createOperationTarget();
    result.setOperation(op);
    return result;
  }
}
