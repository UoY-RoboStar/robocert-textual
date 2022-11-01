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
import circus.robocalc.robochart.RoboChartFactory;

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
    final var ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");

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
    final var ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");

    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getControllers().add(ctrl);

    return ctrl;
  }
}
