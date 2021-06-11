package robocalc.robocert.generator.csp

import javax.inject.Inject
import robocalc.robocert.generator.utils.VariableExtensions
import circus.robocalc.robochart.Variable
import robocalc.robocert.generator.utils.TargetExtensions
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator
import robocalc.robocert.model.robocert.TargetInstantiation
import robocalc.robocert.model.robocert.Target

/**
 * Generates CSP referring to a target.
 */
class TargetGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension TargetExtensions
	@Inject extension VariableExtensions

	/**
	 * Generates an invocation of this target actor's open form with all of
	 * its constants assigned values either from the instantiation or from
	 * the top-level instantiations.csp file.
	 * 
	 * @param it             the target actor for which we are generating a
	 *                       closed form.
	 * @param instantiation  the instantiation (may be null).
	 * 
	 * @return CSP calling into the 'closed' form of this target.
	 */
	def CharSequence generateClosedTarget(Target it,
		TargetInstantiation instantiation) '''«generateOpenTargetName»«FOR c : uninstantiatedConstants.toIterable BEFORE '(' SEPARATOR ',' AFTER ')'»
				«instantiation.generateConstant(c)»
			«ENDFOR»
		'''

	/**
	 * @return generated CSP for the 'open' form of a sequence's target.
	 * 
	 * @param it  the context for which we are generating CSP.
	 */
	def CharSequence generateOpenTargetDef(Target it) '''
		«generateOpenTargetSig» =
			«generateOpenTargetBody»
	'''

	def CharSequence generateOpenTargetSig(Target it) '''
	OpenTarget«FOR c : uninstantiatedConstants.toIterable BEFORE '(' SEPARATOR ',' AFTER ')'»
		«c.constantId»
	«ENDFOR»'''

	def CharSequence generateOpenTargetBody(Target it) '''
		«namespace»::O__(
			{- id -} 0«FOR c : constants.toIterable BEFORE ',' SEPARATOR ','»
				«instantiation.generateConstant(c)»
			«ENDFOR»
		)
	'''

	private def generateOpenTargetName(Target it) '''«sequence.name»::OpenTarget'''

	private def generateConstant(TargetInstantiation it, Variable const) '''{- «const.name» -} «generateValue(const)»'''

	/**
	 * Generates the value of a constant given an instantiation.
	 * 
	 * If the value isn't available, we emit the constant ID; this will
	 * resolve either to a parameter (when defining an open target) or a
	 * definition in instantiations.csp (when defining a closed target).
	 * 
	 * @param it     the instantiation (may be null).
	 * @param const  the constant whose value is requested.
	 * 
	 * @return  a CSP string expanding to the value of the constant.
	 */
	private def CharSequence generateValue(TargetInstantiation it, Variable const) {
		it?.getConstant(const)?.compileExpression(it) ?: const.constantId
	}
}
