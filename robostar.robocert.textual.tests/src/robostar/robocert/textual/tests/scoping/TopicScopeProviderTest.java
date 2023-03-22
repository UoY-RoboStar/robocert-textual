/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.scoping;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.common.matchers.Matchers.hasScope;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.*;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.textual.scoping.TopicScopeProvider;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;
import robostar.robocert.tests.examples.ForagingExample;
import robostar.robocert.util.resolve.EndIndex;

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

  private Gate gate;
  private MessageOccurrence target;

  @BeforeEach
  void init() {
    gate = certFactory.createGate();
    target = msgFactory.occurrence(certFactory.createTargetActor());
  }

  /**
   * Tests that the event scope for a module is always the robotic platform.
   * <p>
   * This is an oddity related to the fact that the relationship between the module and platform in
   * RoboCert is different from that between the two in RoboChart (in the latter, the module
   * contains the platform; in the former, the platform is effectively the module's world.
   */
  @Test
  void testEventScope_module() {
    final var mod = targetFactory.module(example.foraging);

    final var topic = msgFactory.eventTopic(null);
    wrap(mod, msgFactory.message(gate, target, topic));

    assertThat(scope.eventScope(topic, EndIndex.From), hasScope(example.platformObstacle));
    assertThat(scope.eventScope(topic, EndIndex.To), hasScope(example.platformObstacle));
  }

  /**
   * Tests that the topic scope for a controller is the controller.
   */
  @Test
  void testEventScope_controller() {
    final var ctrl = targetFactory.controller(example.obstacleAvoidance);

    final var topic = msgFactory.eventTopic(null);
    wrap(ctrl, msgFactory.message(gate, target, topic));

    assertThat(scope.eventScope(topic, EndIndex.From), hasScope(example.obstacleAvoidanceObstacle));
    assertThat(scope.eventScope(topic, EndIndex.To), hasScope(example.obstacleAvoidanceObstacle));
  }

  private void wrap(Target tgt, Message msg) {
    // TODO(@MattWindsor91): unify this with the existing wrapping logic, which we can't use
    // because uses messages to perform the wrapping

    final var grp = certFactory.createSpecificationGroup();
    grp.setName("Grp");
    grp.setTarget(tgt);

    final var ms = certFactory.createExtensionalMessageSet();
    ms.getMessages().add(msg);
    final var nms = certFactory.createNamedMessageSet();
    nms.setName("Msgs");
    nms.setSet(ms);

    grp.getActors().add(target.getActor());
    grp.getMessageSets().add(nms);
  }
}
