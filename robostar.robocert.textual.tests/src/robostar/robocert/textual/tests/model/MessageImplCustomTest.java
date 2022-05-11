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

package robostar.robocert.textual.tests.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.ComponentActor;
import robostar.robocert.EventTopic;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.TargetActor;
import robostar.robocert.World;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests custom functionality on Messages.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class MessageImplCustomTest {
  @Inject
  private MessageFactory msgFactory;
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private TargetFactory targetFactory;

  private World comWorld;
  private TargetActor comTarget;

  private World collWorld;
  private ComponentActor c1;
  private ComponentActor c2;

  @BeforeEach
  void setUp() {
    final var stm1 = chartFactory.createStateMachineDef();
    stm1.setName("Stm1");
    final var stm2 = chartFactory.createStateMachineDef();
    stm2.setName("Stm1");

    final var ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");
    ctrl.getMachines().addAll(List.of(stm1, stm2));

    setUpComponent(ctrl);
    setUpCollection(stm1, stm2, ctrl);
  }

  private void setUpComponent(ControllerDef ctrl) {
    final var comTgt = targetFactory.controller(ctrl);
    final var comGrp = certFactory.createSpecificationGroup();
    comGrp.setName("ComGroup");
    comGrp.setTarget(comTgt);

    comWorld = msgFactory.world();
    comWorld.setName("W");
    comTarget = msgFactory.targetActor();
    comTarget.setName("T");
    comGrp.getActors().addAll(List.of(comWorld, comTarget));
  }

  private void setUpCollection(StateMachineDef stm1, StateMachineDef stm2, ControllerDef ctrl) {
    final var collTgt = targetFactory.inController(ctrl);
    final var collGrp = certFactory.createSpecificationGroup();
    collGrp.setName("CollGroup");
    collGrp.setTarget(collTgt);

    collWorld = msgFactory.world();
    collWorld.setName("W");
    c1 = certFactory.createComponentActor();
    c1.setName("C1");
    c1.setNode(stm1);
    c2 = certFactory.createComponentActor();
    c2.setName("C2");
    c2.setNode(stm2);
    collGrp.getActors().addAll(List.of(comWorld, c1, c2));
  }

  @Test
  void testIsOutbound_component() {
    final var e = chartFactory.createEvent();
    e.setName("e");
    final var topic = msgFactory.eventTopic(e);

    // All messages in a component context are outbound.
    final var msg1 = msgFactory.spec(comTarget, comWorld, topic);
    assertThat(msg1.isOutbound(), is(true));

    final var msg2 = msgFactory.spec(comWorld, comTarget, topic);
    assertThat(msg2.isOutbound(), is(true));
  }

  @Test
  void testIsOutbound_collection() {
    final var e = chartFactory.createEvent();
    e.setName("e");
    final var topic = msgFactory.eventTopic(e);

    // All messages with a world (and only those) are outbound:

    final var msg1 = msgFactory.spec(c1, comWorld, topic);
    assertThat(msg1.isOutbound(), is(true));

    final var msg2 = msgFactory.spec(comWorld, c2, topic);
    assertThat(msg2.isOutbound(), is(true));

    final var msg3 = msgFactory.spec(c1, c2, topic);
    assertThat(msg3.isOutbound(), is(false));
  }
}
