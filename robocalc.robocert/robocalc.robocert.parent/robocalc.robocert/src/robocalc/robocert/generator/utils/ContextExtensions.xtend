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
import circus.robocalc.robochart.OperationSig
import circus.robocalc.robochart.Event
import circus.robocalc.robochart.generator.csp.untimed.GeneratorUtils
import com.google.inject.Inject

/**
 * Extension methods for dealing with RoboChart contexts.
 */
class ContextExtensions {
	@Inject GeneratorUtils gu
	
	/**
	 * Gets all local constants in a context.
	 * 
	 * @param it  the context to search.
	 * 
	 * @returns all constants on this context, and any interfaces it provides or
	 *          uses.
	 */
	def Iterator<Variable> allLocalConstants(Context it) {
		gu.allLocalConstants(it).iterator
	}
	
	/**
	 * Gets all events available in a context.
	 * 
	 * @param it  the context to search.
	 * 
	 * @returns all events on this context, and any interfaces it provides,
	 *          uses, or requires.
	 */
	def Iterator<Event> allEvents(Context it) {
		gu.allEvents(it).iterator
	}
	
	/**
	 * Gets all operations available in a context.
	 * 
	 * @param it  the context to search.
	 * 
	 * @returns all operations on this context, and any interfaces it provides,
	 *          uses, or requires.
	 */
	def Iterator<OperationSig> allOperations(Context it) {
		gu.allOperations(it).iterator
	}
}
