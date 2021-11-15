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

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.ExplicitActorPair;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests that the custom version of {@link ExplicitActorPair} implements its
 * various derived methods correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ExplicitActorPairCustomTest {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private MessageFactory mf;

	/**
	 * The actor pair to test.
	 */
	protected ExplicitActorPair it;
	/**
	 * The expected from-actor.
	 */
	protected Actor expectedFrom;
	/**
	 * The expected to-actor.
	 */
	protected Actor expectedTo;

	/**
	 * Tests that the resolved-from for an explicit actor pair is correct.
	 */
	@Test
	void testGetResolvedFrom() {
		assertEquals(expectedFrom, it.getResolvedFrom());
	}

	/**
	 * Tests that the resolved-to for an explicit actor pair is correct.
	 */
	@Test
	void testGetResolvedTo() {
		assertEquals(expectedTo, it.getResolvedTo());
	}

	/**
	 * Initialises the objects used for the test.
	 */
	@BeforeEach
	protected void init() {
		expectedFrom = mf.targetActor();
		expectedTo = mf.worldActor();

		it = rc.createExplicitActorPair();
		it.setFrom(expectedFrom);
		it.setTo(expectedTo);
	}
}
