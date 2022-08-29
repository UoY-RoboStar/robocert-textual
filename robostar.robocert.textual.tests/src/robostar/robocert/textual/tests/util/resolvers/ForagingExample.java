/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.util.resolvers;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;

/**
 * Programmatic encoding of a simplified version of the Buchanan foraging robot case study.
 * <p>
 * This encodes a case study due to Edgar Buchanan, Andrew Pomfret, and Jon Timmis, and initially
 * converted to RoboChart by Alvaro Miyazawa, Pedro Ribeiro, Wei Li, and Ana Cavalcanti.
 *
 * @author Matt Windsor
 */
public class ForagingExample {

  public Event platformObstacle;
  public Event obstacleAvoidanceObstacle;
  public Event avoidObstacle;

  public RCModule foraging;
  public RoboticPlatformDef platform;
  public ControllerDef obstacleAvoidance;
  public StateMachineDef avoid;

  public Connection obstaclePlatformToObstacleAvoidance;
  public Connection obstacleObstacleAvoidanceToAvoid;

  @Inject
  public ForagingExample(RoboChartFactory chartFactory) {
    platformObstacle = chartFactory.createEvent();
    platformObstacle.setName("obstacle");

    avoidObstacle = chartFactory.createEvent();
    avoidObstacle.setName("obstacle");

    setupAvoid(chartFactory);

    obstacleAvoidanceObstacle = chartFactory.createEvent();
    obstacleAvoidanceObstacle.setName("obstacle");

    setupObstacleAvoidance(chartFactory);

    platform = chartFactory.createRoboticPlatformDef();
    platform.setName("Platform");
    platform.getEvents().add(platformObstacle);

    obstaclePlatformToObstacleAvoidance = connection(chartFactory, platformObstacle,
        obstacleAvoidanceObstacle, platform, obstacleAvoidance);

    setupForaging(chartFactory);
  }

  private void setupForaging(RoboChartFactory chartFactory) {
    foraging = chartFactory.createRCModule();
    foraging.setName("Foraging");
    foraging.getNodes().add(obstacleAvoidance);
    foraging.getNodes().add(platform);
    foraging.getConnections().add(obstaclePlatformToObstacleAvoidance);
  }

  private Connection connection(RoboChartFactory cf, Event efrom, Event eto, ConnectionNode from,
      ConnectionNode to) {
    final var result = cf.createConnection();
    result.setEfrom(efrom);
    result.setEto(eto);
    result.setFrom(from);
    result.setTo(to);
    return result;
  }

  private void setupAvoid(RoboChartFactory chartFactory) {
    avoid = chartFactory.createStateMachineDef();
    avoid.setName("Avoid");
    avoid.getEvents().add(avoidObstacle);
  }

  private void setupObstacleAvoidance(RoboChartFactory chartFactory) {
    obstacleAvoidance = chartFactory.createControllerDef();
    obstacleAvoidance.setName("ObstacleAvoidance");
    obstacleAvoidance.getEvents().add(obstacleAvoidanceObstacle);
    obstacleAvoidance.getMachines().add(avoid);

    obstacleObstacleAvoidanceToAvoid = connection(chartFactory, obstacleAvoidanceObstacle,
        avoidObstacle, obstacleAvoidance, avoid);
    obstacleAvoidance.getConnections().add(obstacleObstacleAvoidanceToAvoid);
  }
}
