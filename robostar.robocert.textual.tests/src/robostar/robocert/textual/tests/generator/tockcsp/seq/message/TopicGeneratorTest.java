/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tockcsp.seq.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.message.ActorNamespaceResolver;
import robostar.robocert.textual.generator.tockcsp.seq.message.EventTopicGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.message.TopicGenerator;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.MessageTopic;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.ActorContextFinder;
import robostar.robocert.util.ActorNodeResolver;
import robostar.robocert.util.EventFactory;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.textual.tests.util.DummyEventResolver;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the topic CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class TopicGeneratorTest {

  private TopicGenerator tg;

  @Inject
  private CSPStructureGenerator csp;
  @Inject
  private CTimedGeneratorUtils gu;
  @Inject
  private ActorNamespaceResolver nsResolver;
  @Inject
  private ActorContextFinder ctxResolver;
  @Inject
  private MessageFactory mf;
  @Inject
  private RoboChartFactory rchart;
  @Inject
  private RoboCertFactory rcert;
  @Inject
  private EventFactory eventFactory;

  private ComponentActor c1;
  private ComponentActor c2;

  @BeforeEach
  void setUp() {

    // Can't resolve this automatically, because it depends on a custom event resolver
    final var eg = new EventTopicGenerator(csp, gu, new DummyEventResolver(rchart), nsResolver);
    tg = new TopicGenerator(csp, nsResolver, eg, ctxResolver);

    final var ctrl1 = rchart.createControllerDef();
    ctrl1.setName("C1");

    final var ctrl2 = rchart.createControllerDef();
    ctrl2.setName("C2");

    final var mod = rchart.createRCModule();
    mod.setName("Mod");
    mod.getNodes().addAll(List.of(ctrl1, ctrl2));

    c1 = rcert.createComponentActor();
    c1.setNode(ctrl1);
    c2 = rcert.createComponentActor();
    c2.setNode(ctrl2);
  }

  @Test
  void testBidirectional_rightWayUp() {
    // See DummyEventResolver for an explanation of this magic event set.
    final var efrom = eventFactory.event("bidi1");
    final var eto = eventFactory.event("bidi2");

    final var topic = mf.eventTopic(efrom, eto);
    assertThat(topic, generates("Mod::C1::bidi1.out", c1, c2));
  }

  @Test
  void testBidirectional_wrongWayUp() {
    // See above.
    final var efrom = eventFactory.event("bidi2");
    final var eto = eventFactory.event("bidi1");

    final var topic = mf.eventTopic(efrom, eto);
    assertThat(topic, generates("Mod::C1::bidi1.in", c2, c1));
  }

  private Matcher<MessageTopic> generates(String expected, Actor from, Actor to) {
    return generatesCSP(expected, x -> tg.generate(x, from, to));
  }
}
