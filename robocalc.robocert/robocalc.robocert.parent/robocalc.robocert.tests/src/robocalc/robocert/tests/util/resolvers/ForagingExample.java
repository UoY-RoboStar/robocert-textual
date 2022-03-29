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

package robocalc.robocert.tests.util.resolvers;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;

/**
 * Programmatic encoding of a simplified version of the Buchanan foraging robot case study.
 */
public class ForagingExample {

  public Event obstacleAvoidanceObstacle;
  public Event avoidObstacle;

  public RoboticPlatformDef platform;
  public ControllerDef obstacleAvoidance;
  public StateMachineDef avoid;

  @Inject
  public ForagingExample(RoboChartFactory chartFactory) {

    avoidObstacle = chartFactory.createEvent();
    avoidObstacle.setName("obstacle");

    avoid = chartFactory.createStateMachineDef();
    avoid.setName("Avoid");
    avoid.getEvents().add(avoidObstacle);

    obstacleAvoidanceObstacle = chartFactory.createEvent();
    obstacleAvoidanceObstacle.setName("obstacle");

    obstacleAvoidance = chartFactory.createControllerDef();
    obstacleAvoidance.setName("ObstacleAvoidance");
    obstacleAvoidance.getEvents().add(obstacleAvoidanceObstacle);
    obstacleAvoidance.getMachines().add(avoid);

    platform = chartFactory.createRoboticPlatformDef();
    platform.setName("Platform");

    final var foraging = chartFactory.createRCModule();
    foraging.setName("Foraging");
    foraging.getNodes().add(obstacleAvoidance);
    foraging.getNodes().add(platform);
  }
}
