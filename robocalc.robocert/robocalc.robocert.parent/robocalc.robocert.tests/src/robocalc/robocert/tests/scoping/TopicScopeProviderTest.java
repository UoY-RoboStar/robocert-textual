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

package robocalc.robocert.tests.scoping;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.Matchers.hasScope;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.model.robocert.util.TargetFactory;
import robocalc.robocert.scoping.TopicScopeProvider;
import robocalc.robocert.tests.RoboCertInjectorProvider;
import robocalc.robocert.tests.util.resolvers.ForagingExample;

/**
 * Tests {@link TopicScopeProvider}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class TopicScopeProviderTest {

  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private TopicScopeProvider scope;
  @Inject
  private MessageFactory msgFactory;
  @Inject
  private TargetFactory targetFactory;
  @Inject
  private ForagingExample example;


  /**
   * Tests that the topic scope for a module is always the module.
   */
  @Test
  void testTopicScope_module() {
    final var mod = targetFactory.module(example.foraging);
    final var world = certFactory.createWorld();
    final var target = certFactory.createTargetActor();
    wrapActors(mod, world, target);

    final var topic = msgFactory.eventTopic(null);
    msgFactory.spec(world, target, topic);
    assertThat(scope.getEventScope(topic, true), hasScope(example.obstacleAvoidanceObstacle));
    assertThat(scope.getEventScope(topic, false), hasScope(example.obstacleAvoidanceObstacle));
  }

  private void wrapActors(Target t, World world, TargetActor target) {
    final var sgroup = certFactory.createSpecificationGroup();
    sgroup.setName("Grp");
    sgroup.setTarget(t);
    sgroup.getActors().add(world);
    sgroup.getActors().add(target);
  }
}
