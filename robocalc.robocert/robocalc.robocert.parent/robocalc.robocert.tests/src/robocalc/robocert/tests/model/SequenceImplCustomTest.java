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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Sequence;
import robocalc.robocert.model.robocert.StandardActor;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on Sequences, and also tests that the factory
 * resolves it correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class SequenceImplCustomTest {
	@Inject
	private RoboCertFactory rf;
	@Inject
	private MessageFactory mf;

	private Sequence sequence;
	private StandardActor target;
	private StandardActor world;

	@BeforeEach
	void setUp() {
		target = mf.targetActor();
		world = mf.worldActor();

		sequence = rf.createSequence();
		sequence.getActors().addAll(List.of(target, world));
	}

	/**
	 * Tests that the 'targetActor' derived property pulls targets correctly.
	 */
	@Test
	void testTargetActor() {
		assertEquals(target, sequence.getTargetActor());
	}

	/**
	 * Tests that the 'targetActor' derived property returns null for an empty
	 * sequence.
	 */
	@Test
	void testTargetActor_empty() {
		assertNull(rf.createSequence().getTargetActor());
	}

	/**
	 * Tests that the 'targetActor' derived property pulls targets correctly.
	 */
	@Test
	void testWorldActor() {
		assertEquals(world, sequence.getWorldActor());
	}

	/**
	 * Tests that the 'targetActor' derived property returns null for an empty
	 * sequence.
	 */
	@Test
	void testWorldActor_empty() {
		assertNull(rf.createSequence().getWorldActor());
	}
}