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
package robostar.robocert.textual.tests.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on target actors, and also tests that
 * the factory resolves them correctly.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class TargetActorImplCustomTest {
	@Inject
	private RoboCertFactory rf;

	@Test
	void testToString() {
		final var actor = rf.createTargetActor();
		assertThat(actor.toString(), is(equalTo("<<target>> (untitled)")));
		actor.setName("test");
		assertThat(actor.toString(), is(equalTo("<<target>> test")));
	}
}
