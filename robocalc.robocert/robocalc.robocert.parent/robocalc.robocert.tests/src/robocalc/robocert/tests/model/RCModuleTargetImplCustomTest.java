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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.RoboChartFactory;
import robocalc.robocert.model.robocert.RCModuleTarget;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on RCModuleTargets, and also tests that the
 * factory resolves them correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class RCModuleTargetImplCustomTest {
	@Inject
	private RoboCertFactory rf;
	@Inject
	private RoboChartFactory cf;

	private Controller ctrl1;
	private Controller ctrl2;
	private RCModuleTarget example;

	/**
	 * Tests that element and module give the same, non-null result.
	 */
	@Test
	void testElement() {
		final var element = example.getElement();
		assertNotNull(element);
		assertEquals(example.getModule(), element);
	}
	
	/**
	 * Tests that the component collection has the controllers we
	 * expect.
	 */
	@Test
	void testComponents() {
		var components = example.getComponents();
		assertThat(components.size(), is(2));
		assertThat(components, hasItems(ctrl1, ctrl2));
	}

	/**
	 * Tests that the string representation is correct.
	 */
	@Test
	void testToString() {
		assertEquals("module foo", example.toString());
	}

	@BeforeEach
	void setUp() {
		ctrl1 = cf.createControllerDef();
		ctrl1.setName("ctrl1");

		ctrl2 = cf.createControllerDef();
		ctrl2.setName("ctrl2");

		final var module = cf.createRCModule();
		module.setName("foo");
		module.getNodes().addAll(List.of(ctrl1, ctrl2));

		example = rf.createRCModuleTarget();
		example.setModule(module);
	}
}