package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Target
import javax.inject.Inject
import robocalc.robocert.generator.utils.VariableExtensions
import robocalc.robocert.model.robocert.OverrideTarget
import circus.robocalc.robochart.Variable
import robocalc.robocert.generator.utils.TargetExtensions
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator

/**
 * Generates CSP referring to a target.
 */
class TargetGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension TargetExtensions
	@Inject extension VariableExtensions

	/**
	 * @return generated CSP for a sequence target.
	 * 
	 * @param it  the context for which we are generating CSP.
	 */
	def CharSequence generate(Target it) '''«namespace»::O__(
	    {- id -} 0
		«FOR c : constants.toIterable BEFORE ',' SEPARATOR ','»
			{- «c.name» -} «generateValue(c)»
		«ENDFOR»
	)'''

	/**
	 * Gets the value of a constant that may have been overridden.
	 * 
	 * @param it     the overriding target
	 * @param const  the constant whose value is requested.
	 * 
	 * @return  a CSP string expanding to the value of the constant.
	 */
	private def dispatch CharSequence generateValue(OverrideTarget it, Variable const) {
		overrides.findFirst[key == const]?.value?.compileExpression(it) ?: target.generateValue(const)
	}

	/**
	 * Gets the value of a constant that has not been otherwise overridden.
	 * 
	 * @param it     the target (ignored).
	 * @param const  the constant whose value is requested.
	 * 
	 * @return  a CSP string expanding to the value of the constant.
	 */
	private def dispatch generateValue(Target it, Variable const) {
		// Delegate by default to instantiations.csp
		const.constantId
	}

}
