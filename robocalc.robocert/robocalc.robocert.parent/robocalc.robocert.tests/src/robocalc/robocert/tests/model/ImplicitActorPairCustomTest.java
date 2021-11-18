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

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ImplicitActorPair;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;
import robocalc.robocert.tests.util.MessageSpecFactory;

/**
 * Tests that the custom version of {@link ImplicitActorPair} implements its
 * various derived methods correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ImplicitActorPairCustomTest {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private MessageSpecFactory msf;
	@Inject
	private MessageFactory mf;

	/**
	 * The actor pair to test.
	 */
	private ImplicitActorPair it;
	/**
	 * The target actor.
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
		// TODO(@MattWindsor91): deduplicate this with DirectionalActorPairCustomTest

		target = rc.createTargetActor();
		world = rc.createWorldActor();

		it = rc.createImplicitActorPair();
		final var spec = mf.spec(mf.opTopic(msf.nullOp()), it);

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
	@Test
	void testGetResolvedFrom() {
		assertEquals(target, it.getResolvedFrom());
	}

	/**
	 * Tests that the resolved-to for an explicit actor pair is correct.
	 */
	@Test
	void testGetResolvedTo() {
		assertEquals(world, it.getResolvedTo());
	}
}
