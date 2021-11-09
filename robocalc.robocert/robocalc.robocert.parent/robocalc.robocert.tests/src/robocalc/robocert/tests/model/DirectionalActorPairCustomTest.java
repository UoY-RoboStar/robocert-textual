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
 *   mattbw - initial definition
 ********************************************************************************/
package robocalc.robocert.tests.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.DirectionalActorPair;
import robocalc.robocert.model.robocert.MessageDirection;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.World;
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
	protected DirectionalActorPair it;
	/**
	 * The target.
	 */
	protected Target target;
	/**
	 * The world.
	 */
	protected World world;

	/**
	 * Initialises the objects used for the test.
	 */
	@BeforeEach
	protected void init() {
		// TODO(@MattWindsor91): deduplicate this with ImplicitActorPairCustomTest

		target = msf.target();
		world = rc.createWorld();

		it = mf.directional(MessageDirection.INBOUND);
		var spec = mf.spec(mf.eventTopic(msf.intEvent()), it);

		var act = rc.createArrowAction();
		act.setBody(spec);

		var step = rc.createActionStep();
		step.setAction(act);

		var seq = rc.createSequence();

		var sseq = rc.createSubsequence();
		sseq.getSteps().add(step);

		seq.setBody(sseq);

		var sg = rc.createSequenceGroup();
		sg.setTarget(target);
		sg.setWorld(world);
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
