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

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;

/**
 * Programmatic module examples used in tests.
 *
 * @author Matt Windsor
 */
public class Modules {

  /**
   * Creates a module with two directly nested controllers.
   *
   * @param chartFactory factory used to build the module.
   * @return a module laid out as described above.
   */
  public static RCModule directControllers(RoboChartFactory chartFactory) {
    final var c1 = chartFactory.createControllerDef();
    c1.setName("C1");
    final var c2 = chartFactory.createControllerDef();
    c2.setName("C2");

    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");
    mod.getNodes().addAll(List.of(c1, c2));

    return mod;
  }


  /**
   * Creates a module with two indirectly referenced controllers.
   *
   * @param chartFactory factory used to build the module.
   * @return a module laid out as described above.
   */
  public static RCModule indirectControllers(RoboChartFactory chartFactory) {
    final var c1 = chartFactory.createControllerDef();
    c1.setName("C1");
    final var c2 = chartFactory.createControllerDef();
    c2.setName("C2");

    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getControllers().addAll(List.of(c1, c2));


    final var c1r = chartFactory.createControllerRef();
    c1r.setName("R1");
    c1r.setRef(c1);
    final var c2r = chartFactory.createControllerRef();
    c2r.setName("R2");
    c2r.setRef(c2);

    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");
    mod.getNodes().addAll(List.of(c1r, c2r));

    return mod;
  }
}
