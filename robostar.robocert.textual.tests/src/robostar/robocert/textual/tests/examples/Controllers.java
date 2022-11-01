/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.examples;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;

/**
 * Programmatic examples used in tests.
 *
 * @author Matt Windsor
 */
public class Controllers {

  /**
   * Creates a controller nested within a module.
   *
   * @param chartFactory factory used to build the controller.
   * @return controller nested in module.
   */
  public static ControllerDef nested(RoboChartFactory chartFactory) {
    final ControllerDef ctrl = ctrl(chartFactory);

    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");
    mod.getNodes().add(ctrl);

    return ctrl;
  }

  /**
   * Creates a controller within a package.
   *
   * @param chartFactory factory used to build the controller.
   * @return controller inside package.
   */
  public static ControllerDef packaged(RoboChartFactory chartFactory) {
    final ControllerDef ctrl = ctrl(chartFactory);

    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getControllers().add(ctrl);

    return ctrl;
  }

  /**
   * Creates a controller with two directly embedded subcomponents (an operation and a state
   * machine).
   *
   * @param chartFactory factory used to build the controller.
   * @return a controller laid out as described above.
   */
  public static ControllerDef directSubcomponents(RoboChartFactory chartFactory) {
    final StateMachineDef stm = stm(chartFactory);
    final OperationDef op = op(chartFactory);

    liftToPackage(chartFactory, stm, op);

    final ControllerDef ctl = ctrl(chartFactory);
    ctl.getMachines().add(stm);
    ctl.getLOperations().add(op);

    return ctl;
  }

  private static ControllerDef ctrl(RoboChartFactory chartFactory) {
    final var ctl = chartFactory.createControllerDef();
    ctl.setName("Ctrl");
    return ctl;
  }

  private static void liftToPackage(RoboChartFactory chartFactory, StateMachineDef stm,
      OperationDef op) {
    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getMachines().add(stm);
    pkg.getOperations().add(op);
  }

  private static OperationDef op(RoboChartFactory chartFactory) {
    final var op = chartFactory.createOperationDef();
    op.setName("O");
    return op;
  }

  private static StateMachineDef stm(RoboChartFactory chartFactory) {
    final var stm = chartFactory.createStateMachineDef();
    stm.setName("S");
    return stm;
  }

  /**
   * Creates a controller with two indirectly referenced subcomponents (an operation and a state
   * machine).
   *
   * @param chartFactory factory used to build the controller.
   * @return a controller laid out as described above.
   */
  public static ControllerDef indirectSubcomponents(RoboChartFactory chartFactory) {
    final StateMachineDef stm = stm(chartFactory);
    final OperationDef op = op(chartFactory);

    liftToPackage(chartFactory, stm, op);

    final var sr = chartFactory.createStateMachineRef();
    sr.setName("R1");
    sr.setRef(stm);
    final var or = chartFactory.createOperationRef();
    or.setName("R2");
    or.setRef(op);

    final ControllerDef ctl = ctrl(chartFactory);
    ctl.getMachines().add(sr);
    ctl.getLOperations().add(or);

    return ctl;
  }
}
