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
package robocalc.robocert.tests.generator.tockcsp.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.UnaryCorePropertyGenerator;
import robocalc.robocert.model.robocert.ProcessCSPFragment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.UnaryCorePropertyType;
import robocalc.robocert.tests.util.CSPNormaliser;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link UnaryCorePropertyGenerator} on a few properties concerning a
 * {@link ProcessCSPFragment}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class UnaryCorePropertyGeneratorTest {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private CSPNormaliser cn;
	@Inject
	private UnaryCorePropertyGenerator gen;

	private ProcessCSPFragment source;

	@BeforeEach
	void setUp() {
		source = rc.createProcessCSPFragment();
		source.setName("test");
		source.setContents("STOP");
	}

	/**
	 * Tests the generation of determinism assertions.
	 */
	@Test
	void testDeterminism() {
		assertGeneratesBody("test :[deterministic]", UnaryCorePropertyType.DETERMINISM);
	}

	/**
	 * Tests the generation of timed deadlock freedom assertions.
	 */
	@Test
	void testDeadlockFree() {
		assertGeneratesBody(
				"prioritise( test[[tock<-tock,tock<-tock']], <diff(Events,{tock',tock}),{tock}> )\\{tock} :[divergence free [FD]]",
				UnaryCorePropertyType.DEADLOCK_FREE);
	}

	@Test
	void testTimelockFree() {
		assertGeneratesBody("RUN({tock}) ||| CHAOS(diff(Events, {|tock|})) [F= test",
				UnaryCorePropertyType.TIMELOCK_FREE);
	}

	/**
	 * Asserts that generating positive and negative core assertions of the given
	 * type produces, respectively, positive and negative FDR assertions with the
	 * given expected body.
	 *
	 * @param expected expected body, less 'assert', 'not', and tau prioritisation.
	 * @param type     type for which we are testing.
	 */
	private void assertGeneratesBody(String expected, UnaryCorePropertyType type) {
		assertGenerates("assert %s :[tau priority]: {tock}".formatted(expected), type, false);
		assertGenerates("assert not %s :[tau priority]: {tock}".formatted(expected), type, true);
	}

	private void assertGenerates(String expected, UnaryCorePropertyType type, boolean isNegated) {
		final var p = rc.createUnaryCoreProperty();
		p.setNegated(isNegated);
		p.setSubject(source);
		p.setType(type);
		assertEquals(expected, cn.tidy(gen.generate(p)));
	}
}
