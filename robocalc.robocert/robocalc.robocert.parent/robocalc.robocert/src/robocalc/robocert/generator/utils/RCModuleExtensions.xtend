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
import circus.robocalc.robochart.Context
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils

/**
 * Extension methods for dealing with RoboChart modules.
 */
class RCModuleExtensions {
	@Inject extension ControllerExtensions
	@Inject extension CTimedGeneratorUtils
	@Inject extension RoboticPlatformExtensions

	/**
	 * Gets the variables that make up this module's parameterisation.
	 * 
	 * This should align with the definition in the CSP semantics.
	 * 
	 * @param it  the RoboChart module
	 * @return an iterator over (variable, container) pairs.
	 */
	def Iterator<Variable> parameterisation(RCModule it) {
		Iterators.concat(
			platform.allLocalConstants.iterator,
			controllers.iterator.flatMap[moduleParameterisation]
		)
	}
	
	/**
	 * Gets the world from the perspective of a RoboChart module.
	 * 
	 * At time of writing, the world is the robotic platform only.
	 * 
	 * @param it  the RoboChart module
	 * @return an iterator over the contexts making up the world.
	 */
	def Iterator<Context> world(RCModule it) {
		Iterators.singletonIterator(platform)
	}

	/**
	 * Gets the robotic platform definition for a RoboChart module.
	 * 
	 * @param it  the RoboChart module.
	 * @return the module's robotic platform.
	 */
	private def platform(RCModule it) {
		nodes.filter(RoboticPlatform).map[definition].head
	}

	private def controllers(RCModule it) {
		nodes.filter(Controller).map[definition]
	}

}
