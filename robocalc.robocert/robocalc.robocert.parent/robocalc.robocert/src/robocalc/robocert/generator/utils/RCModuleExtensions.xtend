/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.RCModule
import circus.robocalc.robochart.RoboticPlatform
import com.google.common.collect.Iterators
import circus.robocalc.robochart.Controller
import com.google.inject.Inject
import circus.robocalc.robochart.Variable
import java.util.Iterator

/**
 * Extension methods for dealing with RoboChart modules.
 */
class RCModuleExtensions {
	@Inject extension ControllerExtensions
	@Inject extension ContextExtensions
	@Inject extension RoboticPlatformExtensions

	/**
	 * Gets the variables that make up this module's parameterisation.
	 * 
	 * This should align with the definition in the CSP semantics.
	 * 
	 * @param it  the RoboChart module
	 * @return an iterator over the constants in the module's parameterisation.
	 */
	def Iterator<Variable> parameterisation(RCModule it) {
		Iterators.concat(
			platform.allLocalConstants,
			controllers.iterator.flatMap[moduleParameterisation]
		)
	}

	/**
	 * Gets the robotic platform definition for a RoboChart module.
	 * 
	 * @param it  the RoboChart module.
	 * @return the module's robotic platform.
	 */
	private def platform(RCModule it) {
		nodes.filter(RoboticPlatform).map[definition].get(0)
	}

	private def controllers(RCModule it) {
		nodes.filter(Controller).map[definition]
	}

}
