/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.core;

import circus.robocalc.robochart.Variable;
import java.util.Objects;
import java.util.Optional;

/**
 * Generates CSP-M for handling temporary copies of variables.
 *
 * @author Matt Windsor
 */
public class TemporaryVariableGenerator {
	/**
	 * Generates CSP-M for the name of a temporary copy of a variable in an expression.
	 *
	 * The variable must have a name (and this must be checked somewhere).
	 *
	 * @param b the binding for which we are generating.
	 *
	 * @return the generated CSP-M name for the binding in the expression.
	 */
	public CharSequence generateExpressionName(Variable b) {
		Objects.requireNonNull(b, "Tried to use a null variable as an expression");

		final var name = b.getName();
		Objects.requireNonNull(name, "Tried to use a nameless variable as an expression");

		return mangle(name);
	}

	/**
	 * Generates CSP-M for the name of a variable binding in input position.
	 *
	 * If the variable is non-null, its name will be used for the variable; if not, we use
	 * '_' (the CSP-M wildcard bind).
	 *
	 * @param b the binding for which we are generating (may be null).
	 *
	 * @return the generated CSP-M name for the binding.
	 */
	public CharSequence generateInputName(Variable b) {
		// The order here is such that an unnamed binding will become
		// _, not Bnd___.
		return Optional.ofNullable(tryGetName(b)).map(this::mangle).orElse("_");
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
	public CharSequence generateArgumentName(Variable b, long index) {
		// The order here is such that an unnamed binding at position 42 will
		// become Bnd__42, not 42; it is different from the input case.
		final var name = tryGetName(b);
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

	private String tryGetName(Variable b) {
		return b == null ? null : b.getName();
	}
}
