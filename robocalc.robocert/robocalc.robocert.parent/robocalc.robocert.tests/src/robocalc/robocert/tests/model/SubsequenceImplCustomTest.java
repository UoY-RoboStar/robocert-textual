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
package robocalc.robocert.tests.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on Subsequences, and also tests that the
 * factory resolves it correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class SubsequenceImplCustomTest {
	@Inject
	protected RoboCertFactory rf;

	/**
	 * Tests that we return null if there is no parent sequence.
	 */
	@Test
	public void testSequence_Null() {
		assertThat(rf.createSubsequence().getSequence(), is(nullValue()));
	}

	/**
	 * Tests that we can find the subsequence of a root subsequence.
	 */
	@Test
	public void testSequence_RootSubsequence() {
		final var seq = rf.createSequence();
		seq.setBody(rf.createSubsequence());
		assertThat(seq.getBody().getSequence(), is(equalTo(seq)));
	}

	/**
	 * Tests that we can find the subsequence of a singly nested loop subsequence.
	 */
	@Test
	public void testSequence_LoopSubsequence() {
		final var body = rf.createSubsequence();
		final var seq = rf.createSequence();
		final var top = rf.createSubsequence();
		seq.setBody(top);
		final var loop = rf.createLoopStep();
		loop.setBody(body);
		top.getSteps().add(loop);

		assertThat(body.getSequence(), is(equalTo(seq)));
	}
}
