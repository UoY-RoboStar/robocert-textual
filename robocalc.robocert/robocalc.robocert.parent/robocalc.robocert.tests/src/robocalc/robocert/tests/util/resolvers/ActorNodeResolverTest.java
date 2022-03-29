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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.util.ActorNodeResolver;
import robocalc.robocert.model.robocert.util.TargetFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests that the {@link ActorNodeResolver} seems to be resolving things correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class ActorNodeResolverTest {
  // The model used here is a cut down version of the Buchanan et al. foraging robot.

  @Inject
  private ActorNodeResolver resolver;
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private TargetFactory targetFactory;

  private Event obstacleAvoidanceObstacle;
  private Event avoidObstacle;

  private ControllerDef obstacleAvoidance;
  private StateMachineDef avoid;

  @BeforeEach
  void setUp() {
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

    RCModule foraging = chartFactory.createRCModule();
    foraging.setName("Foraging");
    foraging.getNodes().add(obstacleAvoidance);
  }

  @Test
  void testResolve_stateMachine() {
    final var stm = targetFactory.stateMachine(avoid);

    final var world = resolve(world(stm));
    assertThat(world, hasItems(obstacleAvoidance));

    final var target = resolve(target(stm));
    assertThat(target, hasItems(avoid));
  }

  private Set<ConnectionNode> resolve(Actor a) {
    return resolver.resolve(a).collect(Collectors.toUnmodifiableSet());
  }

  /**
   * Creates a dummy world actor for the given target.
   * @param t the target to wrap into an actor.
   * @return the wrapped actor.
   */
  private World world(Target t) {
    final var world = certFactory.createWorld();
    wrapActor(t, world);
    return world;
  }

  /**
   * Creates a dummy target actor for the given target.
   * @param t the target to wrap into an actor.
   * @return the wrapped actor.
   */
  private TargetActor target(Target t) {
    final var targetActor = certFactory.createTargetActor();
    wrapActor(t, targetActor);
    return targetActor;
  }

  private void wrapActor(Target t, Actor a) {
    final var sgroup = certFactory.createSpecificationGroup();
    sgroup.setName("Grp");
    sgroup.setTarget(t);
    sgroup.getActors().add(a);
  }
}
