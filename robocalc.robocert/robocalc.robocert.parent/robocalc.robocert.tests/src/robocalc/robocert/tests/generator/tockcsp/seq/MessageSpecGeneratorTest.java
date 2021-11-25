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

import com.google.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.seq.MessageSpecGenerator;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;
import robocalc.robocert.model.robocert.Argument;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.tests.util.MessageSpecFactory;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.util.MessageFactory;

/**
 * Tests the message spec CSP generator.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class MessageSpecGeneratorTest {
	@Inject private MessageSpecGenerator msg;
	@Inject private MessageFactory mf;
	@Inject private MessageSpecFactory msf;

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a wildcard ('any') argument only.
	 */
	@Test
	void generatePrefixIntEventArrowWithWildcard() {
		assertThat(intSpec(EdgeDirection.INBOUND, msf.wildcardArg()), generatesPrefix("test::event.in?_"));
	}

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a bound wildcard ('any') argument only.
	 */
	@Test
	void generatePrefixIntEventArrowWithBinding() {
		assertThat(intSpec(EdgeDirection.INBOUND, msf.boundArg("A")), generatesPrefix("test::event.in?Bnd__A"));
	}

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	void generatePrefixIntEventArrowWithInt() {
		assertThat(intSpec(EdgeDirection.OUTBOUND, msf.intArg(42)), generatesPrefix("test::event.out.42"));
	}
	
	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	void generateCSPEventSetIntEventArrowWithRest() {
		assertThat(intSpec(EdgeDirection.INBOUND, msf.wildcardArg()), generatesCSPEventSet("{ test::event.in.Bnd__0 | Bnd__0 <- int }"));
	}

	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	void generateCSPEventSetIntEventArrowWithInt() {
		assertThat(intSpec(EdgeDirection.OUTBOUND, msf.intArg(56)), generatesCSPEventSet("{| test::event.out.56 |}"));
	}
	
	private MessageSpec intSpec(EdgeDirection dir, Argument... args) {
		return msf.arrowSpec(mf.eventTopic(msf.intEvent()), dir, args);
	}

	private Matcher<MessageSpec> generatesCSPEventSet(String expected) {
		return generatesCSP(expected, msg::generateCSPEventSet);
	}

	private Matcher<MessageSpec> generatesPrefix(String expected) {
		return generatesCSP(expected, msg::generatePrefix);
	}
}
