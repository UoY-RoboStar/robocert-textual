/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.tests.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.ImplicitEdge;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests that the custom version of {@link ImplicitEdge} implements its various derived methods
 * correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ImplicitEdgeCustomTest {
  @Inject private RoboCertFactory rf;
  @Inject private robocalc.robocert.tests.util.MessageFactory msf;
  @Inject private MessageFactory mf;

  /** The edge to test. */
  private ImplicitEdge it;
  /** The system module actor. */
  private Actor module;
  /** The context actor. */
  private Actor world;

  /** Initialises the objects used for the test. */
  @BeforeEach
  void setUp() {
    module = rf.createTargetActor();
    world = rf.createWorld();

    it = rf.createImplicitEdge();
    final var spec = mf.spec(mf.eventTopic(msf.intEvent()), it);

    final var occ = rf.createMessageOccurrence();
    occ.setMessage(spec);

    final var fragment = rf.createOccurrenceFragment();
    fragment.setOccurrence(occ);

    final var seq = rf.createInteraction();
    seq.getActors().addAll(List.of(module, world));
    seq.getFragments().add(fragment);

    final var sg = rf.createSequenceGroup();
    sg.setTarget(msf.target());
    sg.getActors().addAll(List.of(module, world));
    sg.getInteractions().add(seq);
  }

  /** Tests that the resolved-from for an implicit edge is correct. */
  @ParameterizedTest
  @EnumSource
  void testGetResolvedFrom(EdgeDirection dir) {
    it.setDirection(dir);
    assertEquals(dir == EdgeDirection.INBOUND ? world : module, it.getResolvedFrom());
  }

  /** Tests that the resolved-from for an implicit edge with no set direction is correct. */
  @Test
  void testGetResolvedFrom_default() {
    assertEquals(module, it.getResolvedFrom());
  }

  /** Tests that the resolved-to for an implicit edge is correct. */
  @ParameterizedTest
  @EnumSource
  void testGetResolvedTo(EdgeDirection dir) {
    it.setDirection(dir);
    assertEquals(dir == EdgeDirection.INBOUND ? module : world, it.getResolvedTo());
  }

  /** Tests that the resolved-to for an implicit edge with no set direction is correct. */
  @Test
  void testGetResolvedTo_default() {
    assertEquals(world, it.getResolvedTo());
  }
}
