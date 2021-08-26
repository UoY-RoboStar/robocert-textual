/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.ControllerRef
import circus.robocalc.robochart.ControllerDef
import com.google.inject.Inject
import com.google.common.collect.Iterators
import java.util.Iterator
import circus.robocalc.robochart.Variable
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils

/**
 * Extension methods for dealing with RoboChart controllers.
 */
class ControllerExtensions {
	@Inject extension CTimedGeneratorUtils
	@Inject extension OperationExtensions
	@Inject extension StateMachineExtensions

	/**
	 * Gets the underlying definition of a controller reference.
	 * 
	 * @param it  the controller.
	 * 
	 * @return the definition.
	 */
	def dispatch ControllerDef definition(ControllerRef it) {
		ref
	}

	/**
	 * Gets the underlying definition of a controller definition.
	 * 
	 * @param it  the controller.
	 * 
	 * @return the definition (it).
	 */
	def dispatch ControllerDef definition(ControllerDef it) {
		it
	}

	/**
	 * Gets this controller's contribution to its module's parameterisation.
	 * 
	 * @param it  the controller.
	 * 
	 * @return the iterator of variables that should be added to the module
	 *         parameterisation to account for this controller.
	 */
	def Iterator<Variable> moduleParameterisation(ControllerDef it) {
		Iterators.concat(
			allLocalConstants.iterator,
			machines.flatMap[definition.allLocalConstants].iterator,
			LOperations.flatMap[definition.allLocalConstants].iterator
		)
	}
}
