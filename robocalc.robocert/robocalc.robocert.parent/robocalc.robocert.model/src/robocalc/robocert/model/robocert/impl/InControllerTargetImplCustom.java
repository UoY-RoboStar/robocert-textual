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

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.model.robocert.util.DefinitionResolver;
import robocalc.robocert.model.robocert.util.StreamHelper;

/**
 * Adds derived operation definitions to {@link InControllerTargetImpl}.
 *
 * @author Matt Windsor
 */
public class InControllerTargetImplCustom extends InControllerTargetImpl {
	@Override
	public NamedElement basicGetElement() {
		return getController();
	}

	@Override
	public EList<ConnectionNode> getComponents() {
		final var dr = new DefinitionResolver();
		return StreamHelper.toEList(Stream.concat(
				getController().getLOperations().stream().map(dr::resolve),
				getController().getMachines().stream().map(dr::resolve)
		));
	}

	/**
	 * @return a human-readable summary of this module.
	 */
	@Override
	public String toString() {
		return "components of controller " + getController().getName();
	}
}