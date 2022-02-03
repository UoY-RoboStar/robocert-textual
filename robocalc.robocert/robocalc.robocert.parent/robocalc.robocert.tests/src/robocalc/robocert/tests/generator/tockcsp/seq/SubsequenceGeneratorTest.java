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
package robocalc.robocert.tests.generator.tockcsp.seq;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Subsequence;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests that the concrete implementation of {@link SubsequenceGenerator}
 * behaves appropriately under certain situations.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class SubsequenceGeneratorTest {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private SubsequenceGenerator sg;

	/**
	 * Tests that the empty subsequence becomes the CSP-M 'SKIP_ANYTIME'.
	 */
	@Test
	void testGenerateEmpty() {
		assertThat(rc.createSubsequence(), generates("SKIP_ANYTIME"));
	}
	
	private Matcher<Subsequence> generates(String expected) {
		final var ctx = new LifelineContext(rc.createTargetActor(), 0);
		return generatesCSP(expected, s -> sg.generate(s, ctx));
	}
}
