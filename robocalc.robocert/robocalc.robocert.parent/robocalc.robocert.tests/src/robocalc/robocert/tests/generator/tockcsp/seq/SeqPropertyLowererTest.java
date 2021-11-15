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
package robocalc.robocert.tests.generator.tockcsp.seq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import robocalc.robocert.generator.tockcsp.seq.SeqPropertyLowerer;
import robocalc.robocert.model.robocert.CSPModel;
import robocalc.robocert.model.robocert.CSPProcessSource;
import robocalc.robocert.model.robocert.CSPRefinementOperator;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Sequence;
import robocalc.robocert.model.robocert.SequenceGroup;
import robocalc.robocert.model.robocert.SequenceProperty;
import robocalc.robocert.model.robocert.SequencePropertyType;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests sequence property lowering.
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class SeqPropertyLowererTest {
	@Inject
	private MessageFactory mf;
	@Inject
	private RoboCertFactory rf;
	@Inject
	private RoboChartFactory rcf;
	@Inject
	private SeqPropertyLowerer spl;

	private Sequence sequence;
	private Target target;

	@BeforeEach
	void setUp() {
		target = makeTarget();
		sequence = makeSequence(target);
	}

	/**
	 * Tests that lowering a traces holds property produces the expected refinement.
	 */
	@Test
	void testLower_Traces_Holds() {
		final var prop = property(SequencePropertyType.HOLDS, CSPModel.TRACES);
		assertLower(prop, target, sequence, prop.getModel());
	}

	/**
	 * Tests that lowering a tick-tock is-observed property produces the expected
	 * refinement.
	 */
	@Test
	void testLower_TickTock_IsObserved() {
		final var prop = property(SequencePropertyType.IS_OBSERVED, CSPModel.TICK_TOCK);
		assertLower(prop, sequence, target, prop.getModel());
	}

	/**
	 * Tests that the lowering of the given property has the given LHS, RHS, and
	 * model; that it is a refinement; and that nothing has been set to null along
	 * the way.
	 *
	 * @param p the property to check.
	 * @param l the expected LHS.
	 * @param r the expected RHS.
	 * @param m the expected model.
	 */
	private void assertLower(SequenceProperty p, CSPProcessSource l, CSPProcessSource r, CSPModel m) {
		final var it = spl.lower(p);
		assertNotNull(it);

		final var lhs = it.getLhs();
		assertNotNull(lhs);
		assertEquals(l, lhs);

		final var rhs = it.getRhs();
		assertNotNull(rhs);
		assertEquals(r, rhs);

		final var model = it.getModel();
		assertNotNull(model);
		assertEquals(m, model);

		assertEquals(CSPRefinementOperator.REFINES, it.getType());
	}

	private SequenceProperty property(SequencePropertyType t, CSPModel m) {
		final var p = rf.createSequenceProperty();
		p.setSequence(sequence);
		p.setType(t);
		p.setModel(m);
		return p;
	}

	private Sequence makeSequence(Target t) {
		final var s = rf.createSequence();
		s.setName("seq");
		s.setGroup(group(t));
		s.getActors().addAll(List.of(mf.targetActor(), mf.worldActor()));
		return s;
	}

	private SequenceGroup group(Target t) {
		final var g = rf.createSequenceGroup();
		g.setTarget(t);
		return g;
	}

	private Target makeTarget() {
		final var t = rf.createRCModuleTarget();
		t.setModule(module());
		return t;
	}

	private RCModule module() {
		final var m = rcf.createRCModule();
		m.setName("mod");
		return m;
	}
}