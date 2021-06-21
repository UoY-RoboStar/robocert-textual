package robocalc.robocert.generator.utils

import circus.robocalc.robochart.Variable
import java.util.Iterator
import robocalc.robocert.model.robocert.RCModuleTarget
import robocalc.robocert.model.robocert.Target
import java.util.Collections
import com.google.inject.Inject
import robocalc.robocert.model.robocert.TargetInstantiation
import circus.robocalc.robochart.Expression
import circus.robocalc.robochart.Context

/**
 * Extension methods for dealing with targets.
 */
class TargetExtensions {
	// TODO(@MattWindsor91): move some of these to the metamodel?
	@Inject extension RCModuleExtensions
	@Inject extension VariableExtensions

	/**
	 * Gets the constants for a module target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return an iterator of all constants defined on this target's module.
	 */
	def dispatch getConstants(RCModuleTarget it) {
		module?.parameterisation ?: Collections.emptyIterator
	}

	/**
	 * Gets the constants for an otherwise-unsupported target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return nothing.
	 */
	def dispatch getConstants(Target it) {
		Collections.emptyIterator
	}

	/**
	 * Scrapes the namespace from a RoboChart module.
	 * 
	 * @param it  the actor for which we are getting a namespace.
	 * 
	 * @return the module name (as the namespace of any communications over the module).
	 */
	def dispatch String getNamespace(RCModuleTarget it) {
		module.name
	}

	/**
	 * Fallback for targets that don't correspond to a namespace.
	 * 
	 * @param it  the target for which we are getting a namespace.
	 * @return the empty string (signifying this actor has no namespace).
	 */
	def dispatch String getNamespace(Target it) '''{- UNSUPPORTED TARGET: «it» -}'''

	/**
	 * Gets any constants in this target actor that haven't been instantiated
	 * at the sequence level.
	 * 
	 * This iterator should return a stable ordering of uninstantiated constants.
	 * 
	 * @param it  the target actor.
	 * @return an iterator of uninstantiated constant names.
	 */
	def Iterator<Variable> uninstantiatedConstants(Target it) {
		val instantiated = instantiation.constants.map[key.constantKey].toSet
		constants.filter[!instantiated.contains(constantKey)]
	}

	/**
	 * Looks up a constant in this instantiation and tries to return its value.
	 * 
	 * @param it     the instantiation.
	 * @param const  the constant to look for.
	 * 
	 * @return the value of the constant, or null if it isn't instantiated
	 *         in this instantiation.
	 */
	def Expression getConstant(TargetInstantiation it, Variable const) {
		constants.findFirst[const.constantEqual(key)]?.value
	}
	
	/**
	 * @return equality testing such that two constants should compare equal
	 * if, and only if, they are referencing the same object.
	 */
	private def constantEqual(Variable it, Variable other) {
		// The normal RoboChart equality test compares by name, which doesn't
		// account for the variables being defined in different contexts.
		constantKey == other.constantKey
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
