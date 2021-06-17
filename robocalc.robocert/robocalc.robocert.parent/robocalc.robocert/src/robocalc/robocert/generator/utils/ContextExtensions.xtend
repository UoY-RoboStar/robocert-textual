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
import circus.robocalc.robochart.OperationSig
import org.eclipse.xtext.xbase.lib.Functions.Function1

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
		allLocal[constants]
	}

	private def constants(BasicContext it) {
		variableList.filter[modifier === VariableModifier.CONST].flatMap[vars].iterator
	}
	
	/**
	 * Gets all operations available in a context.
	 * 
	 * @param it  the context to search.
	 * 
	 * @returns all constants on this context, and any interfaces it provides,
	 *          uses, or requires.
	 */
	def Iterator<OperationSig> allOperations(Context it) {
		all[operations.iterator]
	}
	
	private def<T> all(Context it, Function1<BasicContext, Iterator<T>> f) {
		Iterators.concat(
			allLocal(f),
			RInterfaces.iterator.flatMap[f.apply(it)]
		)
	}
	
	private def<T> allLocal(Context it, Function1<BasicContext, Iterator<T>> f) {
		Iterators.concat(
			f.apply(it),
			PInterfaces.iterator.flatMap[f.apply(it)],
			interfaces.iterator.flatMap[f.apply(it)]
		)		
	}
}
