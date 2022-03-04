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
 *   Alvaro Miyazawa, Pedro Ribeiro - initial definition (RoboChart CSP generator)
 *   Matt Windsor - porting to RoboCert
 ********************************************************************************/
package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;

/**
 * Helper for working with RoboChart variables.
 *
 * @author Matt Windsor
 */
public record VariableHelper(CTimedGeneratorUtils gu) {
	@Inject
	public VariableHelper {
		Objects.requireNonNull(gu);
	}

	/**
	 * Gets the name of this constant in the instantiations file.
	 * 
	 * @param it  the constant variable to name.
	 * 
	 * @return the constant ID.
	 */
	public String constantId(Variable it) {
		Objects.requireNonNull(it, "can't get ID of null constant");
		return "const_" + gu.id(it);
	}
}
