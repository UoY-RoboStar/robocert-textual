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

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Subsequence;
import robocalc.robocert.tests.util.CspNormaliser;
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
	@Inject private RoboCertFactory rc;
	@Inject private SubsequenceGenerator sg;
	@Inject private CspNormaliser n;
	
	/**
	 * Tests that the empty subsequence becomes the CSP-M 'SKIP'.
	 */
	@Test
	void testGenerateEmpty() {
		assertGenerates("SKIP", rc.createSubsequence());
	}

	private void assertGenerates(String expected, Subsequence input) {
		assertEquals(expected, n.tidy(sg.generate(input)));
	}
}
