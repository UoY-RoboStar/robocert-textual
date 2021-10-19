package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.ExpressionArgument
import robocalc.robocert.model.robocert.Argument
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.WildcardArgument

/**
 * Generates fragments of CSP prefixes and event sets relating to arguments.
 */
class ArgumentGenerator {
	@Inject extension ExpressionGenerator
	@Inject extension UnsupportedSubclassHandler
	
	/**
	 * Generates an expression argument in prefix position.
	 * 
	 * Right now, we just generate the expression inline and hope the result
	 * is well-formed CSP.  This may eventually change.
	 * 
	 * @param it  the expression argument.
	 * 
	 * @return  generated CSP-M for the expression argument.
	 */
	def dispatch generateForPrefix(ExpressionArgument it) {
		generateExpressionArgument
	}	
	
	/**
	 * Generates a binding argument in prefix position.
	 * 
	 * This expands to an input at the CSP level.  If there is a name
	 * associated with the binding, the input reflects it, with the intent that
	 * the introduced variable is then used to store the input to memory.
	 * Otherwise, the input is a wildcard.
	 * 
	 * @param it  the binding argument.
	 * 
	 * @return  generated CSP-M for the binding argument.
	 */	
	def dispatch generateForPrefix(WildcardArgument it)
		'''?«IF name === null»_«ELSE»«comprehensionVar(name, -1)»«ENDIF»'''
	
	// the -1 above is arbitrary; that code should not be reached.
	
	/**
	 * Generates an unsupported argument in prefix position.
	 * 
	 * @param it  the argument.
	 * 
	 * @return  fallback CSP-M for the argument.
	 */	
	def dispatch generateForPrefix(Argument it) {
		unsupported("prefix argument", "0")
	}

	/**
	 * Generates an expression argument in event set position.
	 * 
	 * This is exactly the same as generating in prefix position, for now.
	 * 
	 * @param it     the expression argument.
	 * @param index  ignored for expression arguments.
	 * 
	 * @return  generated CSP-M for the expression argument.
	 */
	def dispatch generateForSet(ExpressionArgument it, int index) {
		generateExpressionArgument
	}	
	
	/**
	 * Generates a binding argument in prefix position.
	 * 
	 * This expands to a wildcard input at the CSP level.
	 * 
	 * @param it     the wildcard argument.
	 * @param index  the index of the argument, used to determine what
	 *               the name of the comprehension binding is.

	 * 
	 * @return  generated CSP-M for the wildcard argument.
	 */	
	def dispatch generateForSet(WildcardArgument it, int index)
		'''.«comprehensionVar(name, index)»'''
	
	/**
	 * Generates an unsupported argument in prefix position.
	 * 
	 * @param it     the argument.
	 * @param index  ignored for unknown arguments.
	 * 
	 * @return  fallback CSP-M for the argument.
	 */	
	def dispatch generateForSet(Argument it, int index) {
		unsupported("set argument", "0")
	}

	private def generateExpressionArgument(ExpressionArgument it)
		'''.«expr.generate»'''
	
	/**
	 * Generates the set comprehension variable for some bound or wildcard
	 * variable.
	 * 
	 * If the name is non-null, it will be used for the variable; otherwise,
	 * we use the index.
	 *
	 * This is chosen to be short, but unlikely to be used anywhere else.
	 *
	 * @param name   the optional name of the variable.
	 * @param index  the index of the variable.
	 * 
	 * @return  the name that will be assigned to the set comprehension
	 *          variable for that index.
	 */
	def comprehensionVar(String name, int index) '''Bnd__«name ?: index.toString»'''
}