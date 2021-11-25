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
package robocalc.robocert.tests.generator.tockcsp.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.LogicalOperator;
import robocalc.robocert.model.robocert.RelationOperator;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link ExpressionGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class ExpressionGeneratorTest {
	@Inject
	private ExpressionFactory ef;
	@Inject
	private ExpressionGenerator eg;

	/**
	 * Tests that generating the Boolean literals works properly.
	 */
	@Test
	void testGenerateBoolExprs() {
		assertThat(ef.bool(true), generates("true"));
		assertThat(ef.bool(false), generates("false"));
	}

	/**
	 * Tests that generating integer literals works properly.
	 */
	@Test
	void testGenerateIntExprs() {
		assertThat(ef.integer(0), generates("0"));
		assertThat(ef.integer(42), generates("42"));
		// This last case is unlikely to happen in practice, because the parser
		// would consider -1 to be -(1).
		assertThat(ef.integer(-1), generates("-1"));
	}

	/**
	 * Tests that generating minus expressions works properly.
	 */
	@Test
	void testGenerateMinusExprs() {
		assertThat(ef.minus(ef.integer(1)), generates("-(1)"));
		assertThat(ef.minus(ef.minus(ef.integer(42))), generates("-(-(42))"));
		assertThat(ef.minus(ef.constant("x")), generates("-(const_x)"));
	}

	/**
	 * Tests that generating logical expressions works properly.
	 */
	@Test
	void testGenerateLogicalExprs() {
		assertThat(ef.logic(LogicalOperator.AND, ef.bool(true), ef.bool(false)), generates("(true) and (false)"));
		assertThat(ef.logic(LogicalOperator.OR, ef.binding("x"), ef.constant("y")), generates("(Bnd__x) or (const_y)"));
	}

	/**
	 * Tests that generating relational expressions works properly.
	 */
	@Test
	void testGenerateRelationExprs() {
		assertThat(ef.rel(RelationOperator.LE, ef.integer(42), ef.integer(56)), generates("(42) <= (56)"));
	}

	/**
	 * Shortcut for building the Hamcrest matcher for expressions.
	 *
	 * @param expected the expected output.
	 * @param input    the input expression.
	 *
	 * @return the matcher.
	 */
	private Matcher<CertExpr> generates(String expected) {
		return generatesCSP(expected, eg::generate);
	}
}
