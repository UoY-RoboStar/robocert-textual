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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.util.CspNormaliser;
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
	private CspNormaliser csp;
	@Inject
	private RoboCertFactory rc;
	
	/**
	 * Tests that generating a load prefix for an empty binding list yields an empty
	 * CSP fragment.
	 */
	@Test
	void testGenerateLoadsEmpty() {
		assertGenerateLoads("", new Binding[] {});
	}
	
	/**
	 * Tests that generating a load prefix for an binding list containing the
	 * same binding multiple times only loads the binding once.
	 */
	@Test
	void testGenerateLoadsDuplicate() {
		// TODO(@MattWindsor91): we're relying on fallback behaviour from the
		// generator where the binding has no parent sequence.  We really need
		// to fix this as it isn't stable.
		
		var b = rc.createBinding();
		b.setName("foo");
		
		assertGenerateLoads("Memory::unknown::foo.get?Bnd__foo ->", new Binding[] {b, b});
	}

	/**
	 * Tests that generating a store prefix for an empty binding list yields an
	 * empty CSP fragment.
	 */
	@Test
	void testGenerateStoresEmpty() {
		assertGenerateStores("", new Binding[] {});
	}
	
	/**
	 * Tests that generating a store prefix for an binding list containing the
	 * same binding multiple times only loads the binding once.
	 */
	@Test
	void testGenerateStoresDuplicate() {
		// TODO(@MattWindsor91): we're relying on fallback behaviour from the
		// generator where the binding has no parent sequence.  We really need
		// to fix this as it isn't stable.
		
		var b = rc.createBinding();
		b.setName("foo");
		
		assertGenerateStores("Memory::unknown::foo.set!Bnd__foo ->", new Binding[] {b, b});
	}

	private void assertGenerateLoads(String expected, Binding[] input) {
		assertEquals(expected, csp.tidy(lsg.generateLoads(Arrays.stream(input))));
	}

	private void assertGenerateStores(String expected, Binding[] input) {
		assertEquals(expected, csp.tidy(lsg.generateStores(Arrays.stream(input))));
	}
}
