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
package robocalc.robocert.tests.generator.tockcsp.memory;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link LoadStoreGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class LoadStoreGeneratorTest {
	@Inject
	private LoadStoreGenerator lsg;
	@Inject
	private RoboCertFactory rc;

	/**
	 * Tests that generating a load prefix for an empty binding list yields an empty
	 * CSP fragment.
	 */
	@Test
	void testGenerateLoadsEmpty() {
		assertThat(List.of(), generatesLoads(""));
	}

	/**
	 * Tests that generating a load prefix for an binding list containing the same
	 * binding multiple times only loads the binding once.
	 */
	@Test
	void testGenerateLoadsDuplicate() {
		// TODO(@MattWindsor91): we're relying on fallback behaviour from the
		// generator where the binding has no parent sequence. We really need
		// to fix this as it isn't stable.

		final var b = rc.createBinding();
		b.setName("foo");

		assertThat(List.of(b, b), generatesLoads("Memory::unknown::foo.get?Bnd__foo ->"));
	}

	/**
	 * Tests that generating a store prefix for an empty binding list yields an
	 * empty CSP fragment.
	 */
	@Test
	void testGenerateStoresEmpty() {
		assertThat(List.of(), generatesStores(""));
	}

	/**
	 * Tests that generating a store prefix for an binding list containing the same
	 * binding multiple times only loads the binding once.
	 */
	@Test
	void testGenerateStoresDuplicate() {
		// TODO(@MattWindsor91): we're relying on fallback behaviour from the
		// generator where the binding has no parent sequence. We really need
		// to fix this as it isn't stable.

		final var b = rc.createBinding();
		b.setName("foo");

		assertThat(List.of(b, b), generatesStores("Memory::unknown::foo.set!Bnd__foo ->"));
	}

	private Matcher<List<Binding>> generatesLoads(String expected) {
		return generatesCSP(expected, x -> lsg.generateLoads(x.stream()));
	}

	private Matcher<List<Binding>> generatesStores(String expected) {
		return generatesCSP(expected, x -> lsg.generateStores(x.stream()));
	}
}
