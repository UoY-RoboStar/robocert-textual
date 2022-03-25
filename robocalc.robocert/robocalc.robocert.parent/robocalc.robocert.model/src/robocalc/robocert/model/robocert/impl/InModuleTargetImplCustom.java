/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
package robocalc.robocert.model.robocert.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RoboticPlatform;
import robocalc.robocert.model.robocert.util.DefinitionResolver;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * Adds derived operation definitions to {@link InModuleTargetImpl}.
 *
 * @author Matt Windsor
 */
class InModuleTargetImplCustom extends InModuleTargetImpl {
	@Override
	public NamedElement basicGetElement() {
		return getModule();
	}

	@Override
	public EList<ConnectionNode> getComponents() {
		return StreamHelpers.toEList(nodes().filter(x -> !(x instanceof RoboticPlatform)));
	}

	private Stream<ConnectionNode> nodes() {
		return getModule().getNodes().stream();
	}

	/**
	 * @return a human-readable summary of this module.
	 */
	@Override
	public String toString() {
		return "components of module " + getModule().getName();
	}
}