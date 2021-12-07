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
package robocalc.robocert.generator.tockcsp.core;

import robocalc.robocert.model.robocert.Binding;

/**
 * Generates CSP-M for handling various aspects of event value bindings.
 *
 * @author Matt Windsor
 */
public class BindingGenerator {
	/**
	 * Generates CSP-M for the name of a binding in an expression.
	 *
	 * The binding must have a name (and this must be checked somewhere).
	 *
	 * @param it the binding for which we are generating.
	 *
	 * @return the generated CSP-M name for the binding in the expression.
	 */
	public CharSequence generateExpressionName(Binding b) {
		var name = tryGetName(b);
		if (name == null)
			throw new NullPointerException("Tried to use a nameless binding as an expression");
		return mangle(name);
	}

	/**
	 * Generates CSP-M for the name of a binding in input position.
	 *
	 * If the binding exists, its name will be used for the variable; if not, we use
	 * '_' (the CSP-M wildcard bind).
	 *
	 * @param it the binding for which we are generating (may be null).
	 *
	 * @return the generated CSP-M name for the binding.
	 */
	public CharSequence generateInputName(Binding b) {
		// The order here is such that an unnamed binding will become
		// _, not Bnd___.
		var name = tryGetName(b);
		return name == null ? "_" : mangle(name);
	}

	/**
	 * Generates CSP-M for the name of a binding in an argument.
	 *
	 * If the binding has a name, it will be used for the variable; otherwise, we
	 * use the supplied index.
	 *
	 * @param b     the binding for which we are generating.
	 * @param index the index of the argument.
	 *
	 * @return the generated CSP-M name for the binding in the argument.
	 */
	public CharSequence generateArgumentName(Binding b, long index) {
		// The order here is such that an unnamed binding at position 42 will
		// become Bnd__42, not 42; it is different from the input case.
		var name = tryGetName(b);
		return mangle(name == null ? Long.toString(index) : name);
	}

	/**
	 * Generates a mangled CSP-M name for a binding.
	 *
	 * This is chosen to be short, but unlikely to be used anywhere else.
	 *
	 * @param name the name of the variable.
	 *
	 * @return the mangled name.
	 */
	private String mangle(String name) {
		return "Bnd__" + name;
	}

	private String tryGetName(Binding b) {
		return b == null ? null : b.getName();
	}
}
