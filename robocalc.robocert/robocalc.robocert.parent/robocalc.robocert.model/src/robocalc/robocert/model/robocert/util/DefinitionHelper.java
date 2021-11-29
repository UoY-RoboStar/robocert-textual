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
 *   mattbw - initial definition
 ********************************************************************************/
package robocalc.robocert.model.robocert.util;

import java.util.Optional;
import java.util.stream.Stream;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.RoboticPlatformRef;

/**
 * Helper class for finding definitions of various RoboChart components.
 * 
 * @author Matt Windsor
 */
public class DefinitionHelper {
	/**
	 * Gets the robotic platform definition for a RoboChart module.
	 *
	 * @param it the RoboChart module.
	 * @return the module's robotic platform, if it has one.
	 */
	public Optional<RoboticPlatformDef> platform(RCModule it) {
		return nodes(it, RoboticPlatform.class).map(this::platformDef).findFirst();
	}

	/**
	 * Gets the controller definitions for a RoboChart module.
	 *
	 * @param it the RoboChart module.
	 * @return the module's controllers.
	 */	
	public Stream<ControllerDef> controllers(RCModule it) {
		return nodes(it, Controller.class).map(this::controllerDef);
	}

	private <T extends ConnectionNode> Stream<T> nodes(RCModule m, Class<T> clazz) {
		if (m == null)
			return Stream.empty();
		return StreamHelpers.filter(m.getNodes().parallelStream(), clazz);
	}

	private RoboticPlatformDef platformDef(RoboticPlatform p) {
		if (p instanceof RoboticPlatformDef d)
			return d;
		if (p instanceof RoboticPlatformRef r)
			return r.getRef();
		throw new IllegalArgumentException("can't get definition of robotic platform %s".formatted(p));
	}
	
	private ControllerDef controllerDef(Controller c) {
		if (c instanceof ControllerDef d)
			return d;
		if (c instanceof ControllerRef r)
			return r.getRef();
		throw new IllegalArgumentException("can't get definition of controller %s".formatted(c));
	}
}
