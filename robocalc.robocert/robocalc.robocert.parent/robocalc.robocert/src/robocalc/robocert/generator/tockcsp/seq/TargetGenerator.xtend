package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.utils.VariableExtensions
import circus.robocalc.robochart.Variable
import robocalc.robocert.generator.utils.TargetExtensions
import robocalc.robocert.model.robocert.Target
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils
import robocalc.robocert.model.robocert.Instantiation
import robocalc.robocert.model.robocert.CertExpr
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator

/**
 * Generates CSP referring to a target.
 */
class TargetGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension TargetExtensions
	@Inject extension VariableExtensions
	@Inject extension CTimedGeneratorUtils

	/**
	 * Generates a target body, given an instantiation.
	 * 
	 * @param it             the target body.
	 * @param instantiation  the instantiation to use.
	 */
	def CharSequence generate(Target it, Instantiation instantiation) '''
		«generateOpenTargetName»«parameterisation.toList.generateOpenTargetParams(instantiation)»
	'''
	
	/*
	 * In email with Pedro (4 Aug): the target of a refinement against
	 * a (simple) specification should usually be unoptimised (D__); model
	 * comparisons should usually be optimised (O__).
	 * 
	 * TODO(@MattWindsor91): eventually, we should be able to select the
	 * optimisation level.
	 */
	def private generateOpenTargetName(Target it) {
		element.getFullProcessName(false)
	}

	def private generateOpenTargetParams(Iterable<Variable> cs, Instantiation instantiation) '''
		«IF cs.isNullOrEmpty»
			(«ID»)
		«ELSE»
			(
				«ID»,
				«FOR c: cs SEPARATOR ','»
					«instantiation.generateConstant(c)»
				«ENDFOR»
			)
		«ENDIF»
	'''
	
	static val ID = "{- id -} 0"

	// TODO(@MattWindsor91): move these two.

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
	def CharSequence generateConstant(Instantiation it, Variable const) {
		it?.getConstant(const)?.generateNamedExpression(const) ?: const.constantId
	}

	private def generateNamedExpression(CertExpr it, Variable const) '''{- «const.constantId» -} «generate»'''
}
