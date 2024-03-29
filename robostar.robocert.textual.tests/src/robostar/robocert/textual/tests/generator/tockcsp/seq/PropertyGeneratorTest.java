/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tockcsp.seq;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tockcsp.seq.PropertyGenerator;
import robostar.robocert.SemanticModel;
import robostar.robocert.Interaction;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.SequenceProperty;
import robostar.robocert.SequencePropertyType;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests sequence property lowering.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class PropertyGeneratorTest {

  private static final String TARGET_CSP = "Test::Closed::Target";
  private static final String SEQUENCE_CSP = "Test::Closed::Seqs::seq";

  @Inject
  private MessageFactory mf;
  @Inject
  private RoboCertFactory rf;
  @Inject
  private RoboChartFactory rcf;
  @Inject
  private PropertyGenerator spl;

  private Interaction sequence;

  @BeforeEach
  void setUp() {
    final var target = makeTarget();
    final SpecificationGroup group = makeGroup(target);
    sequence = makeSequence(group);
  }

  /**
   * Tests that lowering a traces holds property produces the expected refinement.
   */
  @Test
  void testLower_Traces_Holds() {
    final var prop = property(SequencePropertyType.HOLDS, SemanticModel.TRACES);
    assertLower(prop, SEQUENCE_CSP, TARGET_CSP, prop.getModel());
  }

  /**
   * Tests that lowering a traces does-not-hold property produces the expected refinement.
   */
  @Test
  void testLower_Traces_DoesNotHold() {
    final var prop = property(SequencePropertyType.HOLDS, SemanticModel.TRACES);
    prop.setNegated(true);
    assertLower(prop, SEQUENCE_CSP, TARGET_CSP, prop.getModel());
  }

  /**
   * Tests that lowering a tick-tock is-observed property produces the expected refinement.
   */
  @Test
  void testLower_TickTock_IsObserved() {
    final var prop = property(SequencePropertyType.IS_OBSERVED, SemanticModel.TIMED);
    assertLower(prop, TARGET_CSP, SEQUENCE_CSP + ";\nUSTOP", prop.getModel());
  }

  /**
   * Tests that the lowering of the given property has the given LHS, RHS, and model; that it is a
   * refinement; and that nothing has been set to null along the way.
   *
   * @param p the property to check.
   * @param l the expected LHS.
   * @param r the expected RHS.
   * @param m the expected model.
   */
  private void assertLower(SequenceProperty p, CharSequence l, CharSequence r, SemanticModel m) {
    final var it = spl.lower(p);
    assertThat(it, is(notNullValue()));

    final var lhs = it.lhs();
    assertThat(l, is(lhs));

    final var rhs = it.rhs();
    assertThat(r, is(rhs));

    final var model = it.model();
    assertThat(model, is(notNullValue()));
    assertThat(m, is(model));

    assertThat(p.isNegated(), is(it.isNegated()));
  }

  private SequenceProperty property(SequencePropertyType t, SemanticModel m) {
    final var p = rf.createSequenceProperty();
    p.setInteraction(sequence);
    p.setType(t);
    p.setModel(m);
    return p;
  }

  private Interaction makeSequence(SpecificationGroup group) {
    final var s = rf.createInteraction();
    s.setName("seq");
    s.setGroup(group);
    return s;
  }

  private SpecificationGroup makeGroup(Target t) {
    final var g = rf.createSpecificationGroup();
    g.getActors().addAll(mf.systemActors());
    g.setName("Test");
    g.setTarget(t);
    return g;
  }

  private Target makeTarget() {
    final var t = rf.createModuleTarget();
    t.setModule(module());
    return t;
  }

  private RCModule module() {
    final var m = rcf.createRCModule();
    m.setName("mod");
    return m;
  }
}
