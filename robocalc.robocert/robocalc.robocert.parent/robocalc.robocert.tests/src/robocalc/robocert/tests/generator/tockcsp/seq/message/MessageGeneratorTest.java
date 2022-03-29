/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/
package robocalc.robocert.tests.generator.tockcsp.seq.message;

import com.google.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.seq.message.MessageGenerator;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.util.ValueSpecificationFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;
import robocalc.robocert.model.robocert.ValueSpecification;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.util.MessageFactory;

/**
 * Tests the message spec CSP generator.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class MessageGeneratorTest {
	@Inject private MessageGenerator msg;
	@Inject private MessageFactory mf;
	@Inject private ValueSpecificationFactory vf;
	@Inject private robocalc.robocert.tests.util.MessageFactory msf;

	private Actor world;
	private Actor target;

	@BeforeEach
	void setUp() {
		final var group = msf.group();
		final var actors = group.getActors();
		// This should line up with the way msf produces actors for the group.
		target = actors.get(0);
		world = actors.get(1);
	}

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a wildcard ('any') argument only.
	 */
	@Test
	void generatePrefixIntEventArrowWithWildcard() {
		assertThat(intSpec(world, target, vf.wildcard()), generatesPrefix("test::event.in?_"));
	}

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a bound wildcard ('any') argument only.
	 */
	@Test
	void generatePrefixIntEventArrowWithBinding() {
		assertThat(intSpec(world, target, vf.bound(vf.binding("A"))), generatesPrefix("test::event.in?Bnd__A"));
	}

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	void generatePrefixIntEventArrowWithInt() {
		assertThat(intSpec(target, world, vf.integer(42)), generatesPrefix("test::event.out.42"));
	}
	
	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	void generateCSPEventSetIntEventArrowWithRest() {
		assertThat(intSpec(world, target, vf.wildcard()), generatesCSPEventSet("{ test::event.in.Bnd__0 | Bnd__0 <- int }"));
	}

	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	void generateCSPEventSetIntEventArrowWithInt() {
		assertThat(intSpec(target, world, vf.integer(56)), generatesCSPEventSet("{| test::event.out.56 |}"));
	}
	
	private Message intSpec(Actor from, Actor to, ValueSpecification... args) {
		return mf.spec(from, to, mf.eventTopic(msf.intEvent()), args);
	}

	private Matcher<Message> generatesCSPEventSet(String expected) {
		return generatesCSP(expected, msg::generateCSPEventSet);
	}

	private Matcher<Message> generatesPrefix(String expected) {
		return generatesCSP(expected, msg::generatePrefix);
	}
}
