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

import static org.hamcrest.MatcherAssert.assertThat;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.TemporaryVariableGenerator;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the binding CSP generator.
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class TemporaryVariableGeneratorTest {
	@Inject
	private RoboChartFactory rc;
	@Inject
	private TemporaryVariableGenerator bg;

	//
	// Input
	//

	/**
	 * Tests that generating an input name for an empty binding translates into a
	 * '_' in CSP.
	 */
	@Test
	void generateInputWildcard() {
		assertThat(binding(null), generatesCSP("_", bg::generateInputName));
	}

	/**
	 * Tests that generating an input name for a named binding translates into a
	 * deterministic mangled form in CSP.
	 */
	@Test
	void generateInputBound() {
		assertThat(binding("Foo"), generatesCSP("Bnd__Foo", bg::generateInputName));
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
		assertThat(binding(null), generatesCSP("Bnd__0", this::generateArgumentName));
	}

	/**
	 * Tests that generating an input name for a named binding translates into a
	 * deterministic mangled form in CSP.
	 */
	@Test
	void generateArgumentBound() {
		assertThat(binding("Foo"), generatesCSP("Bnd__Foo", this::generateArgumentName));
	}

	//
	// Test helpers
	//

	private CharSequence generateArgumentName(Variable b) {
		return bg.generateArgumentName(b, 0);
	}

	private Variable binding(String n) {
		final var result = rc.createVariable();
		result.setName(n);
		result.setType(rc.createAnyType());
		return result;
	}
}