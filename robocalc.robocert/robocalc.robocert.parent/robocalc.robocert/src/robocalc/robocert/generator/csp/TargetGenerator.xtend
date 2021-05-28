package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.RCModuleTarget
import robocalc.robocert.model.robocert.Target
import java.util.Collections
import javax.inject.Inject
import robocalc.robocert.generator.utils.RCModuleExtensions
import robocalc.robocert.generator.utils.TypeExtensions

/**
 * Generates CSP referring to a target.
 */
class TargetGenerator {
	@Inject extension RCModuleExtensions
	@Inject extension TypeExtensions
	
	/**
	 * @return generated CSP for a sequence target.
	 * 
	 * @param it  the context for which we are generating CSP.
	 */
	def CharSequence generate(Target it) '''«namespace»::O__(
	    {- id -} 0
		«FOR c: constants.toIterable BEFORE ',' SEPARATOR ','»
			{- «c.name» -} «c.type.defaultValue»
		«ENDFOR»
	)'''

	/**
	 * Gets the constants for a module target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return an iterator of all constants defined on this target's module.
	 */
	private def dispatch getConstants(RCModuleTarget it) {
		module.parameterisation
	}

	/**
	 * Gets the constants for an otherwise-unsupported target
	 * @param it  the target for which we are trying to get all constants.
	 * @return nothing.
	 */
	private def dispatch getConstants(Target it) {
		Collections.emptyIterator
	}	
	
	/**
	 * Scrapes the namespace from a RoboChart module.
	 * 
	 * @param it  the actor for which we are getting a namespace.
	 * @return the module name (as the namespace of any communications over the module).
	 */
	def dispatch String getNamespace(RCModuleTarget it) {
		module.name
	}

	/**
	 * Fallback for targets that don't correspond to a namespace.
	 * @param it  the target for which we are getting a namespace.
	 * @return the empty string (signifying this actor has no namespace).
	 */
	def dispatch String getNamespace(Target it) '''{- UNSUPPORTED TARGET: «it» -}'''
}
