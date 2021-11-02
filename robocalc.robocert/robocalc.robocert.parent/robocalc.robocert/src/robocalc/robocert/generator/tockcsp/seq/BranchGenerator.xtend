package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.Branch
import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.model.robocert.EmptyGuard
import robocalc.robocert.model.robocert.ExprGuard
import robocalc.robocert.generator.tockcsp.top.ExpressionGenerator

/**
 * Generates branches and guards.
 */
class BranchGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension SubsequenceGenerator
	
	/**
	 * Generates CSP-M for a branch.
	 * 
	 * @param it the branch for which we are generating CSP-M.
	 * 
	 * @return the generated CSP-M.
	 */
	def CharSequence generate(Branch it) '''
		«guard.generateGuard»«body.generate»
	'''
	
	private def dispatch generateGuard(EmptyGuard _) ''''''
	
	private def dispatch generateGuard(ExprGuard it) '''«expr.generate» & '''
}