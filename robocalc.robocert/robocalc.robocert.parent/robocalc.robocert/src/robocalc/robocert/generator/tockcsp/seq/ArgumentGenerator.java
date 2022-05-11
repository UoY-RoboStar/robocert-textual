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
package robocalc.robocert.generator.tockcsp.seq;

import com.google.inject.Inject;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.ValueSpecification;
import robocalc.robocert.generator.tockcsp.core.TemporaryVariableGenerator;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robostar.robocert.WildcardValueSpecification;

/**
 * Generates fragments of CSP prefixes and event sets relating to value specifications.
 */
public record ArgumentGenerator (TemporaryVariableGenerator bindingGen, ExpressionGenerator exprGen) {
	// TODO(@MattWindsor91): use CSPStructureGenerator

	/**
	 * Constructs a value specification generator.
	 *
	 * @param bindingGen generator for bindings.
	 * @param exprGen generator for expressions.
	 */
	@Inject
	public ArgumentGenerator {}
	
	/**
	 * Generates a value specification in prefix position.
	 * 
	 * @param it  the value specification.
	 * 
	 * @return  generated CSP-M for the value specification.
	 */
	public CharSequence generateForPrefix(ValueSpecification it) {
		if (it instanceof ExpressionValueSpecification e) {
			return "." + exprGen.generate(e.getExpr());
		}
		if (it instanceof WildcardValueSpecification w) {
			/*
			This expands to an input at the CSP level.  If there is a name
			associated with the binding, the input reflects it, with the intent that
			the introduced variable is then used to store the input to memory.
			Otherwise, the input is a wildcard.
			*/
			return "?" + bindingGen.generateInputName(w.getDestination());
		}
		throw new IllegalArgumentException("unsupported value spec in prefix position: %s".formatted(it));
	}

	/**
	 * Generates a value specification in event set position.
	 * 
	 * @param it     the value specification
	 * @param index  the index of the argument, used to determine what
	 *               the name of the comprehension binding is (if any).
	 *
	 * @return  generated CSP-M for the expression argument.
	 */
	public CharSequence generateForSet(ValueSpecification it, long index) {
		if (it instanceof ExpressionValueSpecification e) {
			return "." + exprGen.generate(e.getExpr());
		}
		if (it instanceof WildcardValueSpecification w) {
			// This expands to a reference to an argument in the enclosing set comprehension.
			return "." + bindingGen.generateArgumentName(w.getDestination(), index);
		}
		throw new IllegalArgumentException("unsupported value spec in set position: %s".formatted(it));
	}
}