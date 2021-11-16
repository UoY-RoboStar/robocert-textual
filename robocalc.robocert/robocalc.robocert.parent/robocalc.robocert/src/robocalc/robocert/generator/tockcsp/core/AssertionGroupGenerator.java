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

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.ll.CSPRefinementPropertyGenerator;
import robocalc.robocert.generator.tockcsp.seq.PropertyLowerer;
import robocalc.robocert.model.robocert.Assertion;
import robocalc.robocert.model.robocert.AssertionGroup;
import robocalc.robocert.model.robocert.CSPRefinementProperty;
import robocalc.robocert.model.robocert.CoreProperty;
import robocalc.robocert.model.robocert.Property;
import robocalc.robocert.model.robocert.SequenceProperty;

/**
 * Generates CSP for assertion groups.
 *
 * @author Matt Windsor
 */
public class AssertionGroupGenerator extends GroupGenerator<AssertionGroup> {
	@Inject
	private CSPRefinementPropertyGenerator cg;
	@Inject
	private PropertyLowerer spl;
	@Inject
	private CorePropertyGenerator ug;

	@Override
	protected Stream<CharSequence> generateBodyElements(AssertionGroup group) {
		return group.getAssertions().stream().map(this::generateAssertion);
	}
	
	@Override
	protected boolean isTimed(AssertionGroup group) {
		// Assertions can't, to the best of our knowledge, be timed.
		return false;
	}
	
	@Override
	protected boolean isInModule(AssertionGroup group) {
		// There is no reason to put assertions in modules; in fact, it might
		// be that they're not even allowed to be in modules.
		// Scoping doesn't matter as assertion names are erased at CSP-M level.
		return false;
	}

	@Override
	protected CharSequence typeName(AssertionGroup group) {
		return "ASSERTION";
	}

	/**
	 * @param a the assertion to generate.
	 *
	 * @return generated CSP for the assertion.
	 */
	private CharSequence generateAssertion(Assertion a) {
		return "-- Assertion %s\n%s".formatted(a.getName(), generateProperty(a.getProperty()));
	}

	/**
	 * @param p the property for which we are generating CSP.
	 *
	 * @return generated CSP for one property.
	 */
	private CharSequence generateProperty(Property p) {
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
