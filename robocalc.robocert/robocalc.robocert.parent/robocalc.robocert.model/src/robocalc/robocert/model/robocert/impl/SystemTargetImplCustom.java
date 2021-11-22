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
package robocalc.robocert.model.robocert.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import robocalc.robocert.model.robocert.util.DefinitionHelper;

/**
 * Adds derived operation definitions to {@link SystemTargetImpl}.
 *
 * @author Matt Windsor
 */
class SystemTargetImplCustom extends SystemTargetImpl {
	@Override
	public NamedElement basicGetElement() {
		// TODO(@MattWindsor91): this is actually quite odd - the theoretical
		// element of a system is the system _enclosing_ the module, which is
		// not quite expressible in the RoboChart metamodel.
		return getEnclosedModule();
	}

	@Override
	public EList<ConnectionNode> getComponents() {
		// The only component connected to a system is the module, and that
		// is inexpressible as a connection node (the metamodel instead
		// special-cases it).
		return new BasicEList<>();
	}

	@Override
	public EList<NamedElement> getContextElements() {
		var list = new BasicEList<NamedElement>(1);
		list.add(new DefinitionHelper().platform(getEnclosedModule()));
		return list;
	}

	/**
	 * @return a human-readable summary of this module.
	 */
	@Override
	public String toString() {
		return "system of module " + getEnclosedModule().getName();
	}
}