package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.ExpressionArgument
import robocalc.robocert.model.robocert.WildcardArgument
import robocalc.robocert.model.robocert.Argument
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler

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
	 * Generates a wildcard argument in prefix position.
	 * 
	 * This expands to a wildcard input at the CSP level.
	 * 
	 * @param it  the wildcard argument.
	 * 
	 * @return  generated CSP-M for the wildcard argument.
	 */	
	def dispatch generateForPrefix(WildcardArgument it) '''?_'''
	
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
	 * Generates a wildcard argument in prefix position.
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
		'''.«comprehensionVar(index)»'''
	
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
	 * This is chosen to be short, but unlikely to be used anywhere else.
	 *  
	 * @param index  the index of the variable.
	 *
	 * @return  the name that will be assigned to the set comprehension
	 *          variable for that index.
	 */
	def comprehensionVar(int index) '''Wc__«index»'''
}