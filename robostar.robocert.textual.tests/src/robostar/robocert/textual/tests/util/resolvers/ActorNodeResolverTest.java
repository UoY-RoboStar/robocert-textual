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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

import circus.robocalc.robochart.ConnectionNode;
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
import robostar.robocert.util.ActorNodeResolver;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests that the {@link ActorNodeResolver} seems to be resolving things correctly on
 * {@link ForagingExample}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class ActorNodeResolverTest {
  @Inject
  private ActorNodeResolver resolver;
  @Inject
  private ForagingExample example;
  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private TargetFactory targetFactory;

  /**
   * Tests resolving connection nodes on a state machine in the example.
   */
  @Test
  void testResolve_stateMachine() {
    final var stm = targetFactory.stateMachine(example.avoid);

    final var world = resolve(world(stm));
    assertThat(world, hasItems(example.platform, example.obstacleAvoidance));

    final var target = resolve(target(stm));
    assertThat(target, hasItems(example.avoid));
  }

  private Set<ConnectionNode> resolve(Actor a) {
    return resolver.resolve(a).collect(Collectors.toUnmodifiableSet());
  }

  /**
   * Creates a dummy world actor for the given target.
   *
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
   *
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
