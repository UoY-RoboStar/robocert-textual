/** 
 * Copyright (c) 2021 University of York and others
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * SPDX-License-Identifier: EPL-2.0
 * Contributors:
 * Matt Windsor - initial definition
 */
package robocalc.robocert.tests

import robocalc.robocert.model.robocert.CertPackage
import robocalc.robocert.model.robocert.Subsequence
import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.model.robocert.ActionStep
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.ExpressionArgument
import robocalc.robocert.model.robocert.CertExpr
import static org.junit.jupiter.api.Assertions.*
import org.eclipse.xtext.testing.util.ParseHelper
import com.google.inject.Inject

/** 
 * Boilerplate for doing parser tests.
 * @author Matt Windsor
 */
class ParseTestHelper {
	@Inject ParseHelper<CertPackage> parseHelper
	
	// This class is in Xtend so we can use Xtend templates.
	
	/**
	 * Parses the given input as a CertPackage and does some basic checks.
	 * 
	 * @param input the fully-formed CertPackage to parse.
	 * 
	 * @returns the resulting package, if all is well.
	 */
	def CertPackage parse(CharSequence input) {
		assertDoesNotThrow[parseHelper.parse(input)] => [assertPackageValid]
	}
	
	/**
	 * Asserts that the given package is present and has no errors.
	 * 
	 * @param it the package to check.
	 */
	private def assertPackageValid(CertPackage it) {
		assertNotNull(it)
		val errors = eResource.errors
		assertTrue(errors.empty, '''Unexpected errors: «FOR e: errors SEPARATOR ', '»«e»«ENDFOR»''')
	}
	
	/**
	 * Lifts a subsequence-to-parse into a RoboCert harness.
	 * 
	 * @param subsequence the subsequence to parse.
	 * 
	 * @return a RoboCert script that will exercise the parsing of the
	 *         subsequence.
	 */
	def CharSequence liftSubsequence(CharSequence subsequence) '''
		target module M: Mod
		sequence group X for M:
			use target
			use world
			sequence Y:
				«subsequence»
	'''
	
	/**
	 * Performs the inverse transformation of liftSubsequence.
	 * 
	 * @param it the package returned from parsing a lifted subsequence.
	 * 
	 * @return the unlifted subsequence.
	 */
	def Subsequence unliftSubsequence(CertPackage it) {
		groups.filter(SequenceGroup).get(0).sequences.get(0).body
	}

	/**
	 * Lifts an expression-to-parse into a RoboCert harness.
	 * 
	 * @param subsequence the expression to parse.
	 * 
	 * @return a RoboCert script that will exercise the parsing of the
	 *         expression.
	 */
	def CharSequence liftExpr(CharSequence expr) {
		liftSubsequence('''op Z(«expr»)''')
	}
	
	/**
	 * Performs the inverse transformation of liftExpr.
	 * 
	 * @param it the package returned from parsing a lifted expression.
	 * 
	 * @return the unlifted expression.
	 */
	def CertExpr unliftExpr(CertPackage it) {
		var action = unliftSubsequence.steps.filter(ActionStep).get(0).action;
		if (action instanceof ArrowAction) {
			var arg = action.body.arguments.get(0)
			if (arg instanceof ExpressionArgument) {
				arg.expr
			}
		}
	}
}
