package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.utils.VariableExtensions
import robocalc.robocert.model.robocert.IntExpr
import robocalc.robocert.model.robocert.ConstExpr
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.CertExpr
import robocalc.robocert.model.robocert.BindingExpr
import robocalc.robocert.generator.tockcsp.top.BindingGenerator

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
	@Inject extension BindingGenerator
	@Inject extension VariableExtensions
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates an expression for an integer literal.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */
	def dispatch generate(IntExpr it) {
		value
	}
	
	/**
	 * Generates an expression for a RoboChart constant.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */
	def dispatch generate(ConstExpr it) {
		// TODO(@MattWindsor91): we may eventually need to pass some context
		// here.
		constant.constantId
	}
	
	/**
	 * Generates an expression for a binding.
	 * 
	 * This expression is dependent on the binding having been previously
	 * inserted into the scope of the expression from a memory channel.
	 */
	def dispatch generate(BindingExpr it) {
		source.generateExpressionName ?: unsupported("expression referencing an unnamed wildcard", "0")
	}

	/**
	 * Generates an expression for an unsupported expression.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */	
	def dispatch generate(CertExpr it) {
		unsupported("expression", "0")
	}
}