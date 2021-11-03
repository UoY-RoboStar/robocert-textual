package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.Branch
import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.model.robocert.EmptyGuard
import robocalc.robocert.model.robocert.ExprGuard
import robocalc.robocert.generator.tockcsp.top.ExpressionGenerator
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.Guard
import robocalc.robocert.model.robocert.ElseGuard

/**
 * Generates branches and guards.
 */
class BranchGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension SubsequenceGenerator
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates CSP-M for a branch.
	 * 
	 * @param it the branch for which we are generating CSP-M.
	 * 
	 * @return the generated CSP-M.
	 */
	def CharSequence generate(Branch it) '''«guard.generateGuard»«body.generate»'''

	private def dispatch generateGuard(EmptyGuard _) ''''''

	private def dispatch generateGuard(ExprGuard it) '''«expr.generate» & '''

	/**
	 * Generates CSP-M for an else-guard.
	 * 
	 * This generator assumes that there is at least one expression guard;
	 * this should be checked by validation before generation.
	 * 
	 * @param it the guard to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	private def dispatch generateGuard(
		ElseGuard it) '''{- else -} not «FOR x : neighbourExprGuards BEFORE '(' SEPARATOR 'and' AFTER ')'»(«x.generate»)«ENDFOR» & '''

	private def dispatch generateGuard(Guard it) {
		unsupported("guard", "")
	}

	private def neighbourExprGuards(ElseGuard it) {
		parent.parent.branches.map[guard].filter(ExprGuard).map[expr]
	}
}
