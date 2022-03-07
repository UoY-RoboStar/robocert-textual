/*******************************************************************************
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
 ******************************************************************************/
package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.Variable;

/**
 * Adds constant presence querying to {@link ConstAssignmentImpl}.
 *
 * @author Matt Windsor
 */
public class ConstAssignmentImplCustom extends ConstAssignmentImpl {
	@Override
	public boolean hasConstant(Variable v) {
		// The normal RoboChart equality test compares by name, which doesn't
		// account for the variables being defined in different contexts.
		// So we use object identity here.
		return constants.stream().anyMatch(other -> v == other);
	}
}