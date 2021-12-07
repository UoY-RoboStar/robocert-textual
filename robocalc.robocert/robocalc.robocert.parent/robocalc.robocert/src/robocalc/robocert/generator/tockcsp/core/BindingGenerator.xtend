package robocalc.robocert.generator.tockcsp.core

import robocalc.robocert.model.robocert.Binding

/**
 * Generates CSP-M for handling various aspects of event value bindings.
 */
class BindingGenerator {
	/**
	 * Generates CSP-M for the name of a binding in an expression.
	 * 
	 * The binding must have a name (and this must be checked somewhere).
	 * 
	 * @param it  the binding for which we are generating.
	 * 
	 * @return  the generated CSP-M name for the binding in the expression.
	 */
	def generateExpressionName(Binding it) {
		if (it === null || name === null) {
			throw new NullPointerException("Tried to use a nameless binding as an expression")
		}
		name.mangle
	}

	/**
	 * Generates CSP-M for the name of a binding in input position.
	 * 
	 * If the binding exists, its name will be used for the variable; if not,
	 * we use '_' (the CSP-M wildcard bind).
	 * 
	 * @param it  the binding for which we are generating (may be null).
	 * 
	 * @return  the generated CSP-M name for the binding.
	 */
	def generateInputName(Binding it) {
		it?.name?.mangle ?: "_"
	}

	/**
	 * Generates CSP-M for the name of a binding in an argument.
	 * 
	 * If the binding has a name, it will be used for the variable; otherwise,
	 * we use the supplied index.
	 * 
	 * @param it     the binding for which we are generating.
	 * @param index  the index of the argument.
	 * 
	 * @return  the generated CSP-M name for the binding in the argument.
	 */
	def generateArgumentName(Binding it, long index) {
		(it?.name ?: index.toString).mangle
	}

	/**
	 * Generates a mangled CSP-M name for a binding.
	 * 
	 * This is chosen to be short, but unlikely to be used anywhere else.
	 * 
	 * @param name  the name of the variable.
	 * 
	 * @return  the mangled name.
	 */
	def private mangle(String name) '''Bnd__«name»'''
}
