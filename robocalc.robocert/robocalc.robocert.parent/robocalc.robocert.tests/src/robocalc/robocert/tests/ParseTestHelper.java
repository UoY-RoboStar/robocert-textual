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
package robocalc.robocert.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import circus.robocalc.robochart.Expression;
import com.google.inject.Inject;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.util.ParseHelper;
import robostar.robocert.CertPackage;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.InteractionFragment;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.OccurrenceFragment;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.util.StreamHelper;

/** 
 * Boilerplate for doing parser tests.
 * @author Matt Windsor
 */
public class ParseTestHelper {
	/**
	 * The prefix appended to lifted subsequences.
	 */
	private static final String PREFIX = """
specification group X
  target = module Mod
  actors = {target as T, world as W}
  sequence Y
    actors T, W
""";
	
	// This class is in Xtend so we can use Xtend templates.
	@Inject ParseHelper<CertPackage> parseHelper;
	
	/**
	 * Parses the given input as a CertPackage and does some basic checks.
	 *
	 * @param input the fully-formed CertPackage to parse.
	 *
	 * @return the resulting package, if all is well.
	 */
	public CertPackage parse(CharSequence input) {
		final var pkg = assertDoesNotThrow(() -> parseHelper.parse(input));
		assertPackageValid(pkg);
		return pkg;
	}
	
	/**
	 * Asserts that the given package is present and has no errors.
	 *
	 * @param it the package to check.
	 */
	private void assertPackageValid(CertPackage it) {
		assertNotNull(it);
		final var errors = it.eResource().getErrors();
		assertTrue(errors.isEmpty(), "Unexpected errors: %s".formatted(errors.stream().map(
				Object::toString).collect(Collectors.joining(", "))));
	}

	/**
	 * Lifts a subsequence-to-parse into a RoboCert harness.
	 *
	 * @param subsequence the subsequence to parse.
	 *
	 * @return a RoboCert script that will exercise the parsing of the
	 *         subsequence.
	 */
	public CharSequence liftSubsequence(CharSequence subsequence) {
		return PREFIX + subsequence.toString().indent(4);
	}

	/**
	 * Performs the inverse transformation of liftSubsequence.
	 * 
	 * @param it the package returned from parsing a lifted subsequence.
	 * 
	 * @return the unlifted subsequence.
	 */
	public EList<InteractionFragment> unliftSubsequence(CertPackage it) {
		final var grp = StreamHelper.firstOfClass(it.getGroups().stream(), SpecificationGroup.class);
		if (grp.isEmpty()) {
			throw new IllegalArgumentException("package does not contain a specification group");
		}
		return grp.get().getInteractions().get(0).getFragments();
	}

	/**
	 * Lifts an expression-to-parse into a RoboCert harness.
	 * 
	 * @param expr the expression to parse.
	 * 
	 * @return a RoboCert script that will exercise the parsing of the
	 *         expression.
	 */
	public CharSequence liftExpr(CharSequence expr) {
		return liftSubsequence("T->>W: op Z(%s)".formatted(expr));
	}
	
	/**
	 * Performs the inverse transformation of liftExpr.
	 * 
	 * @param it the package returned from parsing a lifted expression.
	 * 
	 * @return the unlifted expression.
	 */
	public Expression unliftExpr(CertPackage it) {
		final var sseq = unliftSubsequence(it);
		final var oocc = StreamHelper.firstOfClass(sseq.stream(), OccurrenceFragment.class);
		if (oocc.isEmpty()) {
			throw new IllegalArgumentException("subsequence does not contain an occurrence fragment");
		}
		final var occ = oocc.get().getOccurrence();
		if (occ instanceof MessageOccurrence m) {
			final var arg = m.getMessage().getArguments().get(0);
			if (arg instanceof ExpressionValueSpecification e) {
				return e.getExpr();
			}
		}

		throw new IllegalArgumentException("couldn't find expression in lifted code");
	}
}
