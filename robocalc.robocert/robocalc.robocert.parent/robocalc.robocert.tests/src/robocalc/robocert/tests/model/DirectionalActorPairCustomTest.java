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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.DirectionalActorPair;
import robocalc.robocert.model.robocert.MessageDirection;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;
import robocalc.robocert.tests.util.MessageSpecFactory;

/**
 * Tests that the custom version of {@link DirectionalActorPair} implements its
 * various derived methods correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class DirectionalActorPairCustomTest {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private MessageSpecFactory msf;
	@Inject
	private MessageFactory mf;

	/**
	 * The actor pair to test.
	 */
	private DirectionalActorPair it;
	/**
	 * The target.
	 */
	private Actor target;
	/**
	 * The world.
	 */
	private Actor world;

	/**
	 * Initialises the objects used for the test.
	 */
	@BeforeEach
	void init() {
		// TODO(@MattWindsor91): deduplicate this with ImplicitActorPairCustomTest

		target = mf.targetActor();
		world = mf.worldActor();

		it = mf.directional(MessageDirection.INBOUND);
		final var spec = mf.spec(mf.eventTopic(msf.intEvent()), it);

		final var act = rc.createArrowAction();
		act.setBody(spec);

		final var step = rc.createActionStep();
		step.setAction(act);

		final var sseq = rc.createSubsequence();
		sseq.getSteps().add(step);

		final var seq = rc.createSequence();
		seq.setBody(sseq);

		final var sg = rc.createSequenceGroup();
		sg.setTarget(msf.target());
		sg.getActors().addAll(List.of(target, world));
		sg.getSequences().add(seq);
	}

	/**
	 * Tests that the resolved-from for an explicit actor pair is correct.
	 */
	@ParameterizedTest
	@EnumSource
	void testGetResolvedFrom(MessageDirection dir) {
		it.setDirection(dir);
		assertEquals(dir == MessageDirection.INBOUND ? world : target, it.getResolvedFrom());
	}

	/**
	 * Tests that the resolved-to for an explicit actor pair is correct.
	 */
	@ParameterizedTest
	@EnumSource
	void testGetResolvedTo(MessageDirection dir) {
		it.setDirection(dir);
		assertEquals(dir == MessageDirection.INBOUND ? target : world, it.getResolvedTo());
	}

}
