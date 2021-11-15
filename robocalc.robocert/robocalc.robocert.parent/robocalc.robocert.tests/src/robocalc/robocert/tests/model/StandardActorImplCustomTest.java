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

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.StandardActor;
import robocalc.robocert.model.robocert.TargetActorRelationship;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link StandardActor}s, and also tests that
 * the factory resolves them correctly.
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class StandardActorImplCustomTest {
	@Inject
	private MessageFactory mf;

	@Test
	void testToString() {
		final var world = mf.standardActor(TargetActorRelationship.WORLD);
		assertEquals("<<world>> (untitled)", world.toString());
		world.setName("test");
		assertEquals("<<world>> test", world.toString());

		final var target = mf.standardActor(TargetActorRelationship.TARGET);
		assertEquals("<<target>> (untitled)", target.toString());
		target.setName("test");
		assertEquals("<<target>> test", target.toString());
	}
}