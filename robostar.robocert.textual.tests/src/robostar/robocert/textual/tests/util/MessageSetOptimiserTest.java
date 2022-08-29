/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.util;

import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.MessageSet;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.generator.utils.MessageSetOptimiser;
import robostar.robocert.util.SetFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests some aspects of message set optimisation.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class MessageSetOptimiserTest {
	@Inject
	protected RoboCertFactory rf;

	@Inject
	protected SetFactory sf;

	@Inject
	protected MessageSetOptimiser opt;

	/**
	 * Tests optimisation of basic unions.
	 */
	@Test
	void testOptimise_Union() {
		assertThat(sf.union(sf.empty(), sf.empty()), optimisesTo(sf.empty()));
		assertThat(sf.union(sf.universe(), sf.empty()), optimisesTo(sf.universe()));
		assertThat(sf.union(sf.empty(), sf.universe()), optimisesTo(sf.universe()));
		assertThat(sf.union(sf.universe(), sf.universe()), optimisesTo(sf.universe()));
		assertThat(sf.union(single(), sf.empty()), optimisesTo(single()));
		assertThat(sf.union(sf.empty(), single()), optimisesTo(single()));
		assertThat(sf.union(single(), single()), optimisesTo(single())); // ?
		assertThat(sf.union(single(), sf.universe()), optimisesTo(sf.universe()));
		assertThat(sf.union(sf.universe(), single()), optimisesTo(sf.universe()));
	}

	/**
	 * Tests to make sure isActive is handled correctly on intersections.
	 */
	@Test
	void testIsActive_Intersection() {
		assertThat(sf.inter(sf.empty(), sf.empty()), optimisesTo(sf.empty()));
		assertThat(sf.inter(sf.universe(), sf.empty()), optimisesTo(sf.empty()));
		assertThat(sf.inter(sf.empty(), sf.universe()), optimisesTo(sf.empty()));
		assertThat(sf.inter(sf.universe(), sf.universe()), optimisesTo(sf.universe()));
		assertThat(sf.inter(single(), sf.empty()), optimisesTo(sf.empty()));
		assertThat(sf.inter(sf.empty(), single()), optimisesTo(sf.empty()));
		assertThat(sf.inter(single(), sf.universe()), optimisesTo(single()));
		assertThat(sf.inter(sf.universe(), single()), optimisesTo(single()));

		// This term can't be optimised:
		assertThat(sf.inter(single(), single()), optimisesTo(sf.inter(single(), single())));
	}

	/**
	 * Tests to make sure isActive is handled correctly on differences.
	 */
	@Test
	void testIsActive_Difference() {
		assertThat(sf.diff(sf.empty(), sf.empty()), optimisesTo(sf.empty()));
		assertThat(sf.diff(sf.universe(), sf.empty()), optimisesTo(sf.universe()));
		assertThat(sf.diff(sf.empty(), sf.universe()), optimisesTo(sf.empty()));
		assertThat(sf.diff(sf.universe(), sf.universe()), optimisesTo(sf.empty()));
		assertThat(sf.diff(single(), sf.empty()), optimisesTo(single()));
		assertThat(sf.diff(sf.empty(), single()), optimisesTo(sf.empty()));
		assertThat(sf.diff(single(), sf.universe()), optimisesTo(sf.empty()));

		// These terms can't be optimised:
		assertThat(sf.diff(single(), single()), optimisesTo(sf.diff(single(), single())));
		assertThat(sf.diff(sf.universe(), single()), optimisesTo(sf.diff(sf.universe(), single())));
	}

	private ExtensionalMessageSet single() {
		return sf.extensional(List.of(rf.createMessage()));
	}

	private Matcher<MessageSet> optimisesTo(MessageSet m) {
		return MessageSetOptimisesTo.optimisesTo(opt, m);
	}
}
