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

import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatform;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on {@link ModuleTarget}s, and also tests that the
 * factory resolves them correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ModuleTargetImplCustomTest extends TargetImplCustomTest<ModuleTarget> {
	@Inject
	private RoboCertFactory rf;
	@Inject
	private RoboChartFactory cf;

	private Controller ctrl1;
	private Controller ctrl2;
	private RoboticPlatform rp;
	private RCModule module;

	@BeforeEach
	void setUp() {
		ctrl1 = cf.createControllerDef();
		ctrl1.setName("ctrl1");

		ctrl2 = cf.createControllerDef();
		ctrl2.setName("ctrl2");
		
		rp = cf.createRoboticPlatformDef();
		rp.setName("rp");

		module = cf.createRCModule();
		module.setName("foo");
		module.getNodes().addAll(List.of(ctrl1, ctrl2, rp));

		example = rf.createModuleTarget();
		example.setModule(module);
	}

	@Override
	protected ConnectionNode[] expectedComponents() {
		return new ConnectionNode[]{ctrl1, ctrl2};
	}

	@Override
	protected NamedElement[] expectedContextElements() {
		return new NamedElement[] {rp};
	}

	@Override
	protected NamedElement expectedElement() {
		return module;
	}

	@Override
	protected String expectedString() {
		return "module foo";
	}
}