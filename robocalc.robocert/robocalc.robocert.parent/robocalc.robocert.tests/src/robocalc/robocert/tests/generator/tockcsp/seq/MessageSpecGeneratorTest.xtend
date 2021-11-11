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
package robocalc.robocert.tests.generator.tockcsp.seq

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.generator.tockcsp.seq.MessageSpecGenerator
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import static extension org.junit.Assert.assertEquals
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.tests.util.MessageSpecFactory
import robocalc.robocert.tests.util.CSPNormaliser
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.util.MessageFactory

/**
 * Tests the message spec CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider)
class MessageSpecGeneratorTest {
	@Inject extension MessageSpecGenerator
	@Inject extension MessageFactory
	@Inject extension MessageSpecFactory
	@Inject extension CSPNormaliser

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a wildcard ('any') argument only.
	 */
	@Test
	def void generatePrefixIntEventArrowWithWildcard() {
		assertGeneratesPrefix(
			intEvent.eventTopic.arrowSpec(MessageDirection::INBOUND, wildcardArg),
			"test::event.in?_"
		)
	}

	@Test
	def void generatePrefixIntEventArrowWithBinding() {
		assertGeneratesPrefix(
			intEvent.eventTopic.arrowSpec(MessageDirection::INBOUND, boundArg("A")),
			"test::event.in?Bnd__A"
		)
	}

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	def void generatePrefixIntEventArrowWithInt() {
		assertGeneratesPrefix(intEvent.eventTopic.arrowSpec(MessageDirection::OUTBOUND, intArg(42)),
		"test::event.out.42")
	}
	
	def private assertGeneratesPrefix(MessageSpec it, CharSequence expected) {
		expected.assertEquals(generatePrefix.tidy)
	}

	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	def void generateCSPEventSetIntEventArrowWithRest() {
		"{ test::event.in.Bnd__0 | Bnd__0 <- int }".assertEquals(
			intEvent.eventTopic.arrowSpec(MessageDirection::INBOUND, wildcardArg).generateCSPEventSet.tidy)
	}

	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	def void generateCSPEventSetIntEventArrowWithInt() {
		"{| test::event.out.56 |}".assertEquals(
			intEvent.eventTopic.arrowSpec(MessageDirection::OUTBOUND, intArg(56)).generateCSPEventSet.tidy)
	}

}
