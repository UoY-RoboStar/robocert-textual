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

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ImplicitEdge;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;
import robocalc.robocert.tests.util.MessageSpecFactory;

/**
 * Tests that the custom version of {@link ImplicitEdge} implements its
 * various derived methods correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ImplicitEdgeCustomTest {
	@Inject
	private RoboCertFactory rf;
	@Inject
	private MessageSpecFactory msf;
	@Inject
	private MessageFactory mf;

	/**
	 * The edge to test.
	 */
	private ImplicitEdge it;
	/**
	 * The system module actor.
	 */
	private Actor module;
	/**
	 * The context actor.
	 */
	private Actor context;

	/**
	 * Initialises the objects used for the test.
	 */
	@BeforeEach
	void setUp() {
		module = rf.createSystemModuleActor();
		context = rf.createContextActor();

		it = rf.createImplicitEdge();
		final var spec = mf.spec(mf.eventTopic(msf.intEvent()), it);

		final var act = rf.createArrowAction();
		act.setBody(spec);

		final var step = rf.createOccurrenceFragment();
		step.setAction(act);

		final var sseq = rf.createSubsequence();
		sseq.getSteps().add(step);

		final var seq = rf.createSequence();
		seq.getLifelines().addAll(List.of(module, context));
		seq.setBody(sseq);

		final var sg = rf.createSequenceGroup();
		sg.setTarget(msf.target());
		sg.getActors().addAll(List.of(module, context));
		sg.getSequences().add(seq);
	}

	/**
	 * Tests that the resolved-from for an implicit edge is correct.
	 */
	@ParameterizedTest
	@EnumSource
	void testGetResolvedFrom(EdgeDirection dir) {
		it.setDirection(dir);
		assertEquals(dir == EdgeDirection.INBOUND ? context : module, it.getResolvedFrom());
	}

	/**
	 * Tests that the resolved-from for an implicit edge with no set direction is correct.
	 */
	@Test
	void testGetResolvedFrom_default() {
		assertEquals(module, it.getResolvedFrom());
	}
	
	/**
	 * Tests that the resolved-to for an implicit edge is correct.
	 */
	@ParameterizedTest
	@EnumSource
	void testGetResolvedTo(EdgeDirection dir) {
		it.setDirection(dir);
		assertEquals(dir == EdgeDirection.INBOUND ? module : context, it.getResolvedTo());
	}

	/**
	 * Tests that the resolved-to for an implicit edge with no set direction is correct.
	 */
	@Test
	void testGetResolvedTo_default() {
		assertEquals(context, it.getResolvedTo());
	}
}
