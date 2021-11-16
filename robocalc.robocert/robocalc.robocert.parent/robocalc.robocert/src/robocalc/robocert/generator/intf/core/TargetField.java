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
package robocalc.robocert.generator.intf.core;

/**
 * Fields in a target module.
 *
 * @author MattWindsor91
 */
public enum TargetField {
	/**
	 * The definition of the target.
	 */
	DEFINITION,

	/**
	 * The universe event set of the target.
	 */
	UNIVERSE,

	/**
	 * The tick-tock-CSP context.
	 */
	TICK_TOCK_CONTEXT;

	@Override
	public String toString() {
		return switch (this) {
		case DEFINITION -> "Def";
		case UNIVERSE -> "Universe";
		case TICK_TOCK_CONTEXT -> "TTContext";
		default -> throw new IllegalArgumentException("Unexpected value: " + this);
		};
	}
}
