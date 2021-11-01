package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.FinalAction
import robocalc.robocert.model.robocert.WaitAction
import robocalc.robocert.generator.intf.seq.ActionGenerator
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator
import robocalc.robocert.generator.tockcsp.top.ExpressionGenerator

/**
 * Top-level CSP generator for sequence actions.
 */
class ActionGeneratorImpl implements ActionGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension MessageSpecGenerator
	@Inject extension LoadStoreGenerator

	/**
	 * Generates CSP for an arrow action.
	 * 
	 * @param it  the arrow action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(ArrowAction it) '''«body.generatePrefix» -> «generateBindingStores»SKIP'''

	/**
	 * Generates CSP for a final action.
	 * 
	 * @param it   the final action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(FinalAction it) '''STOP'''

	/**
	 * Generates CSP for a wait action.
	 * 
	 * @param it   the wait action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(WaitAction it) '''WAIT(«units.generate»)'''

	/**
	 * Generates fallback CSP for an unsupported action.
	 * 
	 * @param it  the action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(SequenceAction it) '''{- unsupported action: «it» -} STOP'''

}
