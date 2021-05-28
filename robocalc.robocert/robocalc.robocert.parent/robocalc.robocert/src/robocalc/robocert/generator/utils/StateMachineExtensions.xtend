/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.StateMachineRef
import circus.robocalc.robochart.StateMachineDef

/**
 * Extension methods for dealing with RoboChart state machines.
 */
class StateMachineExtensions {
	/**
	 * Gets the underlying definition of a state machine reference.
	 * 
	 * @param it  the state machine.
	 * 
	 * @return the definition.
	 */
	def dispatch StateMachineDef definition(StateMachineRef it) {
		ref
	}

	/**
	 * Gets the underlying definition of a state machine definition.
	 * 
	 * @param it  the state machine.
	 * 
	 * @return the definition (it).
	 */
	def dispatch StateMachineDef definition(StateMachineDef it) {
		it
	}
}
