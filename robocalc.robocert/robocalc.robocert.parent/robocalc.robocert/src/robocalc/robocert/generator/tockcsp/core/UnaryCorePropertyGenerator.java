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
 *   Pedro Ribeiro - initial definition
 *   Matt Windsor - porting to RoboCert
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.core;

import robocalc.robocert.model.robocert.UnaryCoreProperty;

/**
 * Generates unary 'core assertions': high level assertions such as
 * divergence and deadlock freedom.
 * 
 * @author Matt Windsor
 */
public class UnaryCorePropertyGenerator {
	/**
	 * Generates CSP-M for a core property.
	 *
	 * @param p the property in question.
	 *
	 * @return generated CSP-M for a core property.
	 */
	public CharSequence generate(UnaryCoreProperty p) {
		throw new UnsupportedOperationException("TODO: unimplemented");
	}
}
