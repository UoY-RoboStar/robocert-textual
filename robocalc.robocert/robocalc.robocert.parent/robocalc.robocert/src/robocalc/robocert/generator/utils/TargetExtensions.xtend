package robocalc.robocert.generator.utils

import circus.robocalc.robochart.Variable
import java.util.Iterator
import robocalc.robocert.model.robocert.OverrideTarget
import robocalc.robocert.model.robocert.RCModuleTarget
import robocalc.robocert.model.robocert.Target
import java.util.Collections
import com.google.inject.Inject

/**
 * Extension methods for dealing with targets.
 */
class TargetExtensions {
	// TODO(@MattWindsor91): move some of these to the metamodel?
	
	@Inject extension RCModuleExtensions
	
	/**
	 * Gets the constants for an overridden target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return an iterator of all constants defined on this target's module.
	 */
	def dispatch Iterator<Variable> getConstants(OverrideTarget it) {
		target.constants
	}

	/**
	 * Gets the constants for a module target.
	 * @param it  the target for which we are trying to get all constants.
	 * @return an iterator of all constants defined on this target's module.
	 */
	def dispatch getConstants(RCModuleTarget it) {
		module.parameterisation
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
	 * Scrapes the namespace from an overridden target.
	 * 
	 * @param it  the actor for which we are getting a namespace.
	 *
	 * @return the underlying target's namespace.
	 */
	def dispatch String getNamespace(OverrideTarget it) {
		target.namespace
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
}