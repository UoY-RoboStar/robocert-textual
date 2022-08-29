/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.intf.core;

/**
 * Enumeration of fields in the parametric part of a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
public enum SpecGroupParametricField {

	/**
	 * The module in the specification group containing any memory definitions.
	 */
	MEMORY_MODULE,	
	/**
	 * The module in the specification group containing any sequences.
	 */
	INTERACTION_MODULE,
	/**
	 * The process in the specification group representing the target.
	 */
	TARGET;

	@Override
	public String toString() {
		return switch(this) {
			case MEMORY_MODULE -> "Memory";
			case INTERACTION_MODULE -> "Seqs";
			case TARGET -> "Target";
		};
	}
}
