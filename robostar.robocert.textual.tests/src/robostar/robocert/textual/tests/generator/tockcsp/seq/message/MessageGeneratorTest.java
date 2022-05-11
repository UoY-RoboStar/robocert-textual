/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
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
package robostar.robocert.textual.tests.generator.tockcsp.seq.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.seq.message.MessageGenerator;
import robostar.robocert.Message;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.TargetActor;
import robostar.robocert.World;
import robostar.robocert.util.EventFactory;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.util.ValueSpecificationFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the message spec CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class MessageGeneratorTest {

  @Inject
  private MessageGenerator msg;
  @Inject
  private MessageFactory mf;
  @Inject
  private ValueSpecificationFactory vf;
  @Inject
  private RoboChartFactory rchart;
  @Inject
  private RoboCertFactory rcert;
  @Inject
  private TargetFactory targetFactory;
  @Inject
  private EventFactory eventFactory;

  private World world;
  private TargetActor target;
  private Event event;

  @BeforeEach
  void setUp() {
    final var intType = rchart.createPrimitiveType();
    intType.setName("core_int");

    final var irType1 = rchart.createTypeRef();
    irType1.setRef(intType);
    event = eventFactory.event("event", irType1);

    final var irType2 = rchart.createTypeRef();
    irType2.setRef(intType);
    final var cevent = eventFactory.event("cEvent", irType2);

    final var rp = rchart.createRoboticPlatformDef();
    rp.getEvents().add(event);

    final var ctrl = rchart.createControllerDef();
    ctrl.getEvents().add(cevent);

    final var conn = rchart.createConnection();
    conn.setFrom(rp);
    conn.setTo(ctrl);
    conn.setEfrom(event);
    conn.setEto(cevent);

    final var mod = rchart.createRCModule();
    mod.setName("test");
    mod.getNodes().addAll(List.of());
    mod.getConnections().add(conn);

    final var mtarget = targetFactory.module(mod);

    world = rcert.createWorld();
    target = rcert.createTargetActor();

    final var group = rcert.createSpecificationGroup();
    group.getActors().addAll(List.of(world, target));
    group.setTarget(mtarget);
  }

  // We assume that it is impossible through scoping to get an efrom that refers to the controller.

  /**
   * Tests prefix generation of an arrow message set concerning an integer event against an argument
   * list containing a wildcard ('any') argument only.
   */
  @Test
  void generatePrefixIntEventArrowWithWildcard() {
    assertThat(mf.spec(world, target, mf.eventTopic(event), vf.wildcard()),
        generatesPrefix("test::event.in?_"));
  }

  /**
   * Tests prefix generation of an arrow message set concerning an integer event against an argument
   * list containing a bound wildcard ('any') argument only.
   */
  @Test
  void generatePrefixIntEventArrowWithBinding() {
    assertThat(mf.spec(world, target, mf.eventTopic(event), vf.bound(vf.binding("A"))),
        generatesPrefix("test::event.in?Bnd__A"));
  }

  /**
   * Tests prefix generation of an arrow message set concerning an integer event against an argument
   * list containing an integer argument only.
   */
  @Test
  void generatePrefixIntEventArrowWithInt() {
    assertThat(mf.spec(target, world, mf.eventTopic(event), vf.integer(42)),
        generatesPrefix("test::event.out.42"));
  }

  /**
   * Tests event set generation of an arrow message set concerning an integer event against an
   * argument list containing a rest ('...') argument only.
   */
  @Test
  void generateCSPEventSetIntEventArrowWithRest() {
    assertThat(mf.spec(world, target, mf.eventTopic(event), vf.wildcard()),
        generatesCSPEventSet("{ test::event.in.Bnd__0 | Bnd__0 <- core_int }"));
  }

  /**
   * Tests event set generation of an arrow message set concerning an integer event against an
   * argument list containing an integer argument only.
   */
  @Test
  void generateCSPEventSetIntEventArrowWithInt() {
    assertThat(mf.spec(target, world, mf.eventTopic(event), vf.integer(56)),
        generatesCSPEventSet("{| test::event.out.56 |}"));
  }

  private Matcher<Message> generatesCSPEventSet(String expected) {
    return generatesCSP(expected, msg::generateCSPEventSet);
  }

  private Matcher<Message> generatesPrefix(String expected) {
    return generatesCSP(expected, msg::generatePrefix);
  }
}
