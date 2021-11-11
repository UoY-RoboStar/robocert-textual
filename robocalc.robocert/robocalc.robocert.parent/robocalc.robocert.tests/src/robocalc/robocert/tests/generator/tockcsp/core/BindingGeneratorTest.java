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
package robocalc.robocert.tests.generator.tockcsp.core;

import static org.junit.Assert.assertEquals;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.BindingGenerator;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.util.CSPNormaliser;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the binding CSP generator.
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class BindingGeneratorTest {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private BindingGenerator bg;
	@Inject
	private CSPNormaliser csp;

	//
	// Input
	//

	/**
	 * Tests that generating an input name for an empty binding translates into a
	 * '_' in CSP.
	 */
	@Test
	void generateInputWildcard() {
		assertBindingGeneratesInput(null, "_");
	}

	/**
	 * Tests that generating an input name for a named binding translates into a
	 * deterministic mangled form in CSP.
	 */
	@Test
	void generateInputBound() {
		assertBindingGeneratesInput("Foo", "Bnd__Foo");
	}

	//
	// Argument
	//

	/**
	 * Tests that generating an argument name for an empty binding fills in the
	 * index then uses the usual deterministic mangled form
	 */
	@Test
	void generateArgumentWildcard() {
		assertBindingGeneratesArgument(null, "Bnd__0");
	}

	/**
	 * Tests that generating an input name for a named binding translates into a
	 * deterministic mangled form in CSP.
	 */
	@Test
	void generateArgumentBound() {
		assertBindingGeneratesArgument("Foo", "Bnd__Foo");
	}

	//
	// Test helpers
	//

	private void assertBindingGeneratesArgument(String name, CharSequence expected) {
		assertEquals(expected, csp.tidy(bg.generateArgumentName(binding(name), 0)));
	}

	private void assertBindingGeneratesInput(String name, CharSequence expected) {
		assertEquals(expected, csp.tidy(bg.generateInputName(binding(name))));
	}

	private Binding binding(String n) {
		final var result = rc.createBinding();
		result.setName(n);
		return result;
	}
}