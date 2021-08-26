package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.utils.VariableExtensions
import robocalc.robocert.model.robocert.RAIntLit
import robocalc.robocert.model.robocert.RAConstExpr
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.RAExpr

/**
 * The RoboCert expression generator.
 * 
 * RoboCert, for now, has a fairly simple expression language with a separate
 * generator from RoboChart.  This is predominantly to simplify dealing with
 * constants and parameter bindings.
 *
 * In the future, RoboCert may target the RoboChart language and/or generator.
 * It used to in the past, but this proved too complex.
 */
class ExpressionGenerator {
	@Inject extension VariableExtensions
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates an expression for an integer literal.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */
	def dispatch generate(RAIntLit it) '''«value»'''
	
	/**
	 * Generates an expression for an integer literal.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */
	def dispatch generate(RAConstExpr it) {
		// TODO(@MattWindsor91): we may eventually need to pass some context
		// here.
		constant.constantId
	}

	/**
	 * Generates an expression for an unsupported expression.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */	
	def dispatch generate(RAExpr it) {
		unsupported("expression", "0")
	}
}