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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.ImplicitActorPair;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.World;
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
	protected ImplicitActorPair it;
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
		// TODO(@MattWindsor91): deduplicate this with DirectionalActorPairCustomTest

		target = msf.target();
		world = rc.createWorld();

		it = rc.createImplicitActorPair();
		var spec = mf.spec(mf.opTopic(msf.nullOp()), it);

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
