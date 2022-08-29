/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tockcsp.seq;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robostar.robocert.textual.generator.intf.seq.context.ActorContext;
import robostar.robocert.textual.generator.intf.seq.context.InteractionContext;
import robostar.robocert.textual.generator.intf.seq.SubsequenceGenerator;
import robostar.robocert.textual.generator.intf.seq.context.Synchronisation;
import robostar.robocert.InteractionFragment;
import robostar.robocert.ParFragment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.UntilFragment;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

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
