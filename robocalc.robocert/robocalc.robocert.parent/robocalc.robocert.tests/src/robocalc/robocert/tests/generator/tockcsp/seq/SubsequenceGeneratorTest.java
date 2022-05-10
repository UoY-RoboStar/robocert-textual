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

import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.context.ActorContext;
import robocalc.robocert.generator.intf.seq.context.InteractionContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.intf.seq.context.Synchronisation;
import robocalc.robocert.model.robocert.InteractionFragment;
import robocalc.robocert.model.robocert.ParFragment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.UntilFragment;
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
	 * Tests that the empty subsequence becomes the CSP-M 'SKIP'.
	 */
	@Test
	void testGenerateEmpty() {
		assertThat(List.of(), generates("SKIP"));
	}
	
	private Matcher<List<InteractionFragment>> generates(String expected) {
		final var ta = rc.createTargetActor();

		final var seq = rc.createInteraction();
		final var untils = new Synchronisation<UntilFragment>(List.of(), "until", "until");
		final var pars = new Synchronisation<ParFragment>(List.of(), "par", "par");

		final var ictx = new InteractionContext(seq, List.of(ta), untils, pars);
		final var ctx = new ActorContext(ictx, rc.createTargetActor(), "a");
		return generatesCSP(expected, s -> sg.generate(s, ctx));
	}
}
