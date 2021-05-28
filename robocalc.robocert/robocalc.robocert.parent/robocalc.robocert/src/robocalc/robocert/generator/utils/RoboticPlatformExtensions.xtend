/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.RoboticPlatformRef
import circus.robocalc.robochart.RoboticPlatformDef

/**
 * Extension methods for dealing with RoboChart robotic platforms.
 */
class RoboticPlatformExtensions {
	/**
	 * Gets the underlying definition of a robotic platform reference.
	 * 
	 * @param it  the robotic platform.
	 * 
	 * @return the definition.
	 */
	def dispatch RoboticPlatformDef definition(RoboticPlatformRef it) {
		ref
	}

	/**
	 * Gets the underlying definition of a robotic platform definition.
	 * 
	 * @param it  the robotic platform.
	 * 
	 * @return the definition (it).
	 */
	def dispatch RoboticPlatformDef definition(RoboticPlatformDef it) {
		it
	}
}
