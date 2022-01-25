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

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.SequenceGroup;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link SequenceGroup}s,
 * and also tests that the factory resolves it correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class SequenceGroupImplCustomTest {
	@Inject
	private RoboCertFactory rf;

	private SequenceGroup group;
	private TargetActor module;
	private World world;

	@BeforeEach
	void setUp() {
		module = rf.createTargetActor();
		world = rf.createWorld();
		group = rf.createSequenceGroup();
		group.getActors().addAll(List.of(module, world));
	}

	/**
	 * Tests that the 'TargetActor' derived property resolves correctly.
	 */
	@Test
	void testTargetActor() {
		assertThat(group.getTargetActor(), is(equalTo(module)));
	}

	/**
	 * Tests that the 'TargetActor' derived property returns null for an empty
	 * sequence.
	 */
	@Test
	void testTargetActor_empty() {
		assertThat(rf.createSequenceGroup().getTargetActor(), is(nullValue()));
	}

	/**
	 * Tests that the 'contextActor' derived property resolves correctly.
	 */
	@Test
	void testContextActor() {
		assertThat(group.getWorld(), is(equalTo(world)));
	}

	/**
	 * Tests that the 'contextActor' derived property returns null for an empty
	 * sequence.
	 */
	@Test
	void testContextActor_empty() {
		assertThat(rf.createSequenceGroup().getWorld(), is(nullValue()));
	}
}