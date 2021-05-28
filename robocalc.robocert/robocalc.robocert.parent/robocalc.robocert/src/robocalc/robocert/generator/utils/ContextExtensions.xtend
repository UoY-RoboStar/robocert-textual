/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.Variable
import circus.robocalc.robochart.Context
import java.util.Iterator
import com.google.common.collect.Iterators
import circus.robocalc.robochart.BasicContext
import circus.robocalc.robochart.VariableModifier

/**
 * Extension methods for dealing with RoboChart contexts.
 */
class ContextExtensions {
	/**
	 * Gets all local constants in a context.
	 * 
	 * @param it  the context to search.
	 * 
	 * @returns all constants on this context, and any interfaces it provides or
	 *          uses.
	 */
	def Iterator<Variable> allLocalConstants(Context it) {
		Iterators.concat(constants, PInterfaces.iterator.flatMap[constants], interfaces.iterator.flatMap[constants])
	}

	private def constants(BasicContext it) {
		variableList.filter[modifier === VariableModifier.CONST].flatMap[vars].iterator
	}
}
