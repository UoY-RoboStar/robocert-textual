/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import com.google.inject.Inject
import circus.robocalc.robochart.Variable

/**
 * Extension methods for working with RoboChart variables.
 */
class VariableExtensions {
	@Inject extension EObjectExtensions

	/**
	 * Gets the name of this constant in the instantiations file.
	 * 
	 * @param it  the constant variable to name.
	 * 
	 * @return the constant ID.
	 */
	def constantId(Variable it) '''const_«id»'''
}
