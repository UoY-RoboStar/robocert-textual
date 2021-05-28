/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.OperationRef
import circus.robocalc.robochart.OperationDef

/**
 * Extension methods for dealing with RoboChart operations.
 */
class OperationExtensions {
	/**
	 * Gets the underlying definition of an operation reference.
	 * 
	 * @param it  the operation.
	 * 
	 * @return the definition.
	 */
	def dispatch OperationDef definition(OperationRef it) {
		ref
	}

	/**
	 * Gets the underlying definition of an operation definition.
	 * 
	 * @param it  the operation.
	 * 
	 * @return the definition (it).
	 */
	def dispatch OperationDef definition(OperationDef it) {
		it
	}
}
