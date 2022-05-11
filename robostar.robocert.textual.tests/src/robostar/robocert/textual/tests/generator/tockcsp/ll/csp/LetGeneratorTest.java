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
package robostar.robocert.textual.tests.generator.tockcsp.ll.csp;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.LetGenerator;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests {@link LetGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class LetGeneratorTest {
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private LetGenerator lg;

	/**
	 * Tests that a let-within with no bindings resolves to the 'within' body.
	 */
	@Test
	void testEmptyLet() {
		assertThat(lg.let(), generatesCSP("SKIP", this::withinSkip));
	}

	/**
	 * Tests that a let-within with no bindings resolves to a proper let-within.
	 */
	@Test
	void testLargeLet() {
		final var foo0 = csp.definition(csp.function("foo", "0"), "bar");
		final var foo1 = csp.definition(csp.function("foo", "1"), "baz");
		assertThat(lg.let(foo0, foo1), generatesCSP("""
				let
					foo(0) = bar
					foo(1) = baz
				within SKIP""", this::withinSkip));
	}

	private CharSequence withinSkip(LetGenerator.Let l) {
		return l.within("SKIP");
	}
}
