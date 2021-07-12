package robocalc.robocert.generator.csp

import com.google.inject.Inject
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator
import robocalc.robocert.model.robocert.DeadlineStep

/**
 * Generates CSP for deadlines.
 * 
 * Most of this CSP is calls into either the CSP-M or RoboCert standard
 * libraries.
 */
class DeadlineGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension SubsequenceGenerator

	/**
	 * Generates CSP for a deadline step.
	 * 
	 * @param it  the deadline step to generate.
	 * 
	 * @return the generated CSP.
	 */
	def generateDeadline(DeadlineStep it) '''Deadline(
		(
			«body.generate»
		),
		{- time units -} «units.compileExpression(it)»
	)'''
}
