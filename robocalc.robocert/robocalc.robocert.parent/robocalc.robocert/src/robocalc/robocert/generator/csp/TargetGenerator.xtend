package robocalc.robocert.generator.csp

import javax.inject.Inject
import robocalc.robocert.generator.utils.VariableExtensions
import circus.robocalc.robochart.Variable
import robocalc.robocert.generator.utils.TargetExtensions
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator
import robocalc.robocert.model.robocert.TargetInstantiation
import robocalc.robocert.model.robocert.Target
import circus.robocalc.robochart.Expression

/**
 * Generates CSP referring to a target.
 */
class TargetGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension TargetExtensions
	@Inject extension VariableExtensions

	/**
	 * Generates an external reference for the 'closed' form of this target.
	 * 
	 * The closed form has no parameters, with all constants assigned values
	 * either from the target's instantiation or from
	 * the top-level instantiations.csp file.
	 * 
	 * Note that the returned sequence isn't qualified; if calling this from
	 * outside the sequence, qualify with 'sequence.name::'.
	 * 
	 * @param it  the target for which we are generating a closed form.
	 * 
	 * @return CSP referencing the 'closed' form of this target.
	 */
	def CharSequence generateClosedTargetRef(Target it) '''«sequence.name»::Target'''

	/**
	 * Generates a process definition for the 'closed' form of this target.
	 * 
	 * The closed form has no parameters, with all constants assigned values
	 * either from the target's instantiation or from
	 * the top-level instantiations.csp file.
	 * 
	 * @param it  the target for which we are generating a closed form.
	 * 
	 * @return CSP defining the 'closed' form of this target.
	 */
	def CharSequence generateClosedTargetDef(Target it) '''
		Target =
			«generateOpenTargetSig(instantiation)»
	'''

	/**
	 * Generates an external reference for the 'open' form of this target.
	 * 
	 * The open form has parameters exposed, and any reference to it must
	 * fill those parameters using either values in the given instantiation or,
	 * where values are missing, references to the instantiations CSP file.
	 * 
	 * @param it  the context for which we are generating CSP.
	 * 
	 * @return generated CSP for the 'open' form of a sequence's target.
	 */
	def CharSequence generateOpenTargetRef(Target it, TargetInstantiation instantiation) '''
		«sequence.name»::«generateOpenTargetSig(instantiation)»
	'''

	/**
	 * Generates a process definition for the 'open' form of this target.
	 * 
	 * @param it             the target for which we are generating an open
	 *                       form.
	 * @param instantiation  the instantiation (may be null).
	 * 
	 * @return generated CSP for the 'open' form of a sequence's target.
	 */
	def CharSequence generateOpenTargetDef(Target it) '''
		«generateOpenTargetSig(null)» =
			«generateOpenTargetBody»
	'''

	/**
	 * Generates the signature of an open target definition or reference.
	 * 
	 * Because the parameters used in the definition are just the constant IDs,
	 * which are also how we refer to any fallback references to the
	 * instantiations file, both definitions and references can have the same
	 * signature generator.
	 * 
	 * @param it             the target for which we are generating an open
	 *                       form.
	 * @param instantiation  the instantiation (may be null).
	 * 
	 * @return CSP referring to, or giving the signature of, the 'open' form of
	 *         this target.
	 */
	private def CharSequence generateOpenTargetSig(Target it, TargetInstantiation instantiation) '''
	OpenTarget«FOR c : uninstantiatedConstants.toIterable BEFORE '(' SEPARATOR ',' AFTER ')'»
		«instantiation.generateConstant(c)»
	«ENDFOR»'''

	private def CharSequence generateOpenTargetBody(Target it) '''
		«namespace»::O__(
			{- id -} 0«FOR c : constants.toIterable BEFORE ',' SEPARATOR ','»
					«instantiation.generateConstant(c)»
			«ENDFOR»
		)
	'''

	/**
	 * Generates the value of a constant given an instantiation.
	 * 
	 * If the value isn't available, we emit the constant ID; this will
	 * resolve either to a parameter (when defining an open target) or a
	 * definition in instantiations.csp (when defining a closed target).
	 * 
	 * If the value is available, we emit a CSP comment giving the name,
	 * for clarity.
	 * 
	 * @param it     the instantiation (may be null).
	 * @param const  the constant whose value is requested.
	 * 
	 * @return  a CSP string expanding to the value of the constant.
	 */
	private def CharSequence generateConstant(TargetInstantiation it, Variable const) {
		it?.getConstant(const)?.generateNamedExpression(const, it) ?: const.constantId
	}

	private def generateNamedExpression(Expression it, Variable const,
		TargetInstantiation instantiation) '''{- «const.constantId» -} «compileExpression(instantiation)»'''
}
