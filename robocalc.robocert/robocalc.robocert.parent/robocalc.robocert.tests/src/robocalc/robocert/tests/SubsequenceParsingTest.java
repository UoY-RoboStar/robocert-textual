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
package robocalc.robocert.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.Subsequence;

/**
 * Tests the parsing of RoboCert subsequences.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class SubsequenceParsingTest {
	@Inject
	private ParseTestHelper pt;
	@Inject
	private RoboCertFactory rc;
	

	/**
	 * Tests whether parsing empty subsequences works.
	 */
	@Test
	void testParseEmpty() {
		assertParse(rc.createSubsequence(), "nothing");
	}
	
	private void assertParse(Subsequence expected, String input) {
		var result = pt.parse(pt.liftSubsequence(input));
		// can't use normal equality here
		assertTrue(EcoreUtil2.equals(expected, pt.unliftSubsequence(result)));
	}
}
