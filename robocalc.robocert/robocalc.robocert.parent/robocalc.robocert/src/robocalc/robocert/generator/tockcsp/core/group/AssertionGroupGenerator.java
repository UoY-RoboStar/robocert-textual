/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/
package robocalc.robocert.generator.tockcsp.core.group;

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.CorePropertyGenerator;
import robocalc.robocert.generator.tockcsp.seq.PropertyGenerator;
import robostar.robocert.Assertion;
import robostar.robocert.AssertionGroup;
import robostar.robocert.CSPProperty;
import robostar.robocert.CoreProperty;
import robostar.robocert.Property;
import robostar.robocert.SequenceProperty;

/**
 * Generates CSP for assertion groups.
 *
 * @author Matt Windsor
 */
public class AssertionGroupGenerator extends GroupGenerator<AssertionGroup> {
	@Inject
	private PropertyGenerator spl;
	@Inject
	private CorePropertyGenerator ug;

	@Override
	protected Stream<CharSequence> generateBodyElements(AssertionGroup group) {
		return group.getAssertions().stream().map(this::generateAssertion);
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
		if (p instanceof CSPProperty c)
			return generateCSPProperty(c);
		if (p instanceof SequenceProperty s)
			return spl.generate(s);
		if (p instanceof CoreProperty u)
			return ug.generate(u);
		throw new IllegalArgumentException("Unsupported assertion property type: %s (missing match arm?)".formatted(p));
	}

	private String generateCSPProperty(CSPProperty c) {
		return "assert%s %s".formatted(c.isNegated() ? " not" : "", c.getCsp());
	}
}
