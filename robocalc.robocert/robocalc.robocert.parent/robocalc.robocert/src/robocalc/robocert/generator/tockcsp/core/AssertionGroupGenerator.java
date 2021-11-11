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

import java.util.stream.Collectors;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.ll.CSPRefinementPropertyGenerator;
import robocalc.robocert.generator.tockcsp.seq.SeqPropertyLowerer;
import robocalc.robocert.model.robocert.Assertion;
import robocalc.robocert.model.robocert.AssertionGroup;
import robocalc.robocert.model.robocert.CSPRefinementProperty;
import robocalc.robocert.model.robocert.Property;
import robocalc.robocert.model.robocert.SequenceProperty;
import robocalc.robocert.model.robocert.CoreProperty;

/**
 * Generates CSP for assertion groups.
 *
 * @author Matt Windsor
 */
class AssertionGroupGenerator {
	@Inject
	private CSPRefinementPropertyGenerator cg;
	@Inject
	private SeqPropertyLowerer spl;
	@Inject
	private CorePropertyGenerator ug;

	/**
	 * Generates CSP-M for an assertion group.
	 *
	 * @param it the group in question.
	 *
	 * @return generated CSP for the assertion group.
	 */
	public CharSequence generate(AssertionGroup it) {
		var body = it.getAssertions().stream().map(this::generateAssertion).collect(Collectors.joining("\n\n"));
		return String.join("\n", header(it), body, "-- END ASSERTION GROUP");
	}

	private CharSequence header(AssertionGroup it) {
		var name = it.getName();
		return "-- BEGIN ASSERTION GROUP %s".formatted(name == null ? "(untitled)" : name);
	}

	/**
	 * @param a the assertion to generate.
	 *
	 * @return generated CSP for the assertion.
	 */
	private CharSequence generateAssertion(Assertion a) {
		return "-- Assertion %s\n%s".formatted(a.getName(), generateBody(a.getProperty()));
	}

	/**
	 * @param p the property for which we are generating CSP.
	 *
	 * @return generated CSP for one property.
	 */
	private CharSequence generateBody(Property p) {
		// Remember to add new properties as time goes by.
		// TODO(@MattWindsor91): dependency-inject these, somehow
		if (p instanceof CSPRefinementProperty c)
			return cg.generateProperty(c);
		if (p instanceof SequenceProperty s)
			return cg.generateProperty(spl.lower(s));
		if (p instanceof CoreProperty u)
			return ug.generate(u);
		throw new IllegalArgumentException("Unsupported assertion property type: %s (missing match arm?)".formatted(p));
	}
}
