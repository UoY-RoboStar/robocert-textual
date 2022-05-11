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

package robostar.robocert.textual.tests.util.resolvers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Event;
import com.google.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.Actor;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.Target;
import robostar.robocert.TargetActor;
import robostar.robocert.World;
import robostar.robocert.util.resolve.EventResolver;
import robostar.robocert.util.resolve.EventResolverImpl;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests that the {@link EventResolverImpl} seems to be resolving things correctly on {@link
 * ForagingExample}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class EventResolverTest {

  @Inject
  private EventResolver resolver;
  @Inject
  private ForagingExample example;
  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private MessageFactory msgFactory;
  @Inject
  private TargetFactory targetFactory;


  /**
   * Tests resolving connections on a module in the example.
   */
  @Test
  void testResolve_module() {
    final var mod = targetFactory.module(example.foraging);

    final var world = certFactory.createWorld();
    final var target = certFactory.createTargetActor();

    wrapActors(mod, world, target);

    final var conns1 = resolve(example.platformObstacle, example.obstacleAvoidanceObstacle, world,
        target);
    assertThat(conns1, hasItems(example.obstaclePlatformToObstacleAvoidance));

    // This connection is not bidirectional, so this should be empty.
    final var conns2 = resolve(example.obstacleAvoidanceObstacle, example.platformObstacle, target,
        world);
    assertThat(conns2, is(empty()));

    // Inferring an eto.
    final var conns3 = resolve(example.platformObstacle, null, world, target);
    assertThat(conns3, hasItems(example.obstaclePlatformToObstacleAvoidance));
  }

  /**
   * Tests resolving connections on a state machine in the example.
   */
  @Test
  void testResolve_stateMachine() {
    final var stm = targetFactory.stateMachine(example.avoid);

    final var world = certFactory.createWorld();
    final var target = certFactory.createTargetActor();

    wrapActors(stm, world, target);

    final var conns1 = resolve(example.obstacleAvoidanceObstacle, example.avoidObstacle, world,
        target);
    assertThat(conns1, hasItems(example.obstacleObstacleAvoidanceToAvoid));

    // This connection is not bidirectional, so this should be empty.
    final var conns2 = resolve(example.avoidObstacle, example.obstacleAvoidanceObstacle, target,
        world);
    assertThat(conns2, is(empty()));

    // Inferring an eto.
    final var conns3 = resolve(example.obstacleAvoidanceObstacle, null, world, target);
    assertThat(conns3, hasItems(example.obstacleObstacleAvoidanceToAvoid));
  }

  private void wrapActors(Target t, World world, TargetActor target) {
    final var sgroup = certFactory.createSpecificationGroup();
    sgroup.setName("Grp");
    sgroup.setTarget(t);
    sgroup.getActors().add(world);
    sgroup.getActors().add(target);
  }

  private Set<Connection> resolve(Event efrom, Event eto, Actor from, Actor to) {
    final var topic = msgFactory.eventTopic(efrom, eto);
    return resolver.resolve(topic, from, to).collect(Collectors.toUnmodifiableSet());
  }
}
