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
package robocalc.robocert.tests.generator.util.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.utils.name.BindingNamer;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.tests.util.MessageSpecFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests for {@link BindingNamer}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class BindingNamerTest {
	@Inject
	private MessageFactory mf;
	@Inject
	private MessageSpecFactory msf;
	@Inject
	private RoboCertFactory rcf;
	@Inject
	private BindingNamer bx;

	/**
	 * Tests that getting the unambiguous name of a binding with no parent returns
	 * the name of that binding alone.
	 */
	@Test
	public void testGetUnambiguousName_NoParent() {
		final var b = rcf.createBinding();
		b.setName("test");
		assertUnambiguousNameEqual("test", b);
	}

	/**
	 * Tests that getting the unambiguous name of a binding inside the root
	 * subsequence of a sequence diagram gets the expected name.
	 */
	@Test
	public void testGetUnambiguousName_RootSubsequence() {
		final var w = msf.boundArg("test");
		final var aspec = msf.arrowSpec(mf.eventTopic(msf.intEvent()), EdgeDirection.INBOUND, w);
		final var aact = rcf.createArrowAction();
		aact.setBody(aspec);
		final var astep = rcf.createOccurrenceFragment();
		astep.setAction(aact);
		final var ssq = rcf.createSubsequence();
		ssq.getSteps().add(astep);
		final var sq = rcf.createSequence();
		sq.setBody(ssq);

		assertUnambiguousNameEqual("step0_action_body_argument0", w.getBinding());
	}

	private void assertUnambiguousNameEqual(String expected, Binding b) {
		assertEquals(expected, bx.getUnambiguousName(b));
	}
}
