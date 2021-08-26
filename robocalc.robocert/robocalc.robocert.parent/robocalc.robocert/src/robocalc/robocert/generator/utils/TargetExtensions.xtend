package robocalc.robocert.generator.utils

import circus.robocalc.robochart.Variable
import java.util.Iterator
import robocalc.robocert.model.robocert.RCModuleTarget
import robocalc.robocert.model.robocert.Target
import java.util.Collections
import com.google.inject.Inject
import circus.robocalc.robochart.Context
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils
import robocalc.robocert.model.robocert.Instantiation

/**
 * Extension methods for dealing with targets.
 */
class TargetExtensions {
	// TODO(@MattWindsor91): move some of these to the metamodel?
	@Inject extension RCModuleExtensions
	@Inject extension VariableExtensions
	@Inject extension CTimedGeneratorUtils

	/**
	 * Gets the parameterisation for a module target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return an iterator of all constants defined on this target's module.
	 */
	def dispatch getParameterisation(RCModuleTarget it) {
		module?.parameterisation ?: Collections.emptyIterator
	}

	/**
	 * Gets the parameterisation for an otherwise-unsupported target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return nothing.
	 */
	def dispatch getParameterisation(Target it) {
		Collections.emptyIterator
	}

	/**
	 * Gets the namespace of a target.
	 * 
	 * @param it  the target for which we are getting a namespace.
	 * @return the namespace (named element's process ID) of the target.
	 */
	def String getNamespace(Target it) {
		element?.processId
	}

	/**
	 * Gets any constants in this target actor that haven't been instantiated
	 * by the given instantiation.
	 * 
	 * This iterator should return a stable ordering of uninstantiated constants.
	 * 
	 * @param it    the target.
	 * @param inst  the instantiation in question.
	 * @return an iterator of uninstantiated constant names.
	 */
	def Iterator<Variable> uninstantiatedConstants(Target it, Instantiation inst) {
		val instantiated = inst.assignments.flatMap[constants].map[constantKey].toSet
		parameterisation.filter[!instantiated.contains(constantKey)]
	}

	/**
	 * @return a stringification of the given constant so as to be useful for
	 * equality testing in the presence of multiple instances of constants with
	 * the same name but possibly different contexts.
	 */
	private def constantKey(Variable it) {
		// We toString because CharSequences don't compare equal properly.		
		constantId.toString
	}
	
	/**
	 * Gets the world from the perspective of a module target.
	 * 
	 * @param it  the module target in question.
	 * 
	 * @return the world, as an iterator over contexts.
	 */
	def dispatch Iterator<Context> world(RCModuleTarget it) {
		module.world
	}
	
	/**
	 * Gets the fallback world.
	 * 
	 * @param it  the module target in question.
	 * 
	 * @return the world, as an iterator over contexts.
	 */
	def dispatch Iterator<Context> world(Target it) {
		Collections.emptyIterator
	}
}
