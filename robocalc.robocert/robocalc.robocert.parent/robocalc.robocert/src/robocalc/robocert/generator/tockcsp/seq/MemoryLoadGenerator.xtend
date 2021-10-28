package robocalc.robocert.generator.tockcsp.seq

import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.model.robocert.BindingExpr
import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import robocalc.robocert.model.robocert.Binding
import robocalc.robocert.generator.tockcsp.top.BindingGenerator

/**
 * Generates loads from memory to local bindings.
 * 
 * These loads are used to put bindings into scope at the CSP level before they
 * appear in expressions.
 */
class MemoryLoadGenerator {
	@Inject extension BindingGenerator
	@Inject extension MemoryGenerator
	
	/**
	 * Generates a memory load prefix for a set of bindings.
	 * 
	 * This should be inserted before whatever CSP process is going to
	 * reference the bindings.
	 * 
	 * @param bindings  the bindings from which we are generating.
	 * 
	 * @return  the CSP-M prefix.
	 */
	def generate(Binding ...bindings) '''
	«FOR b: bindings SEPARATOR '->\n' AFTER '->\n'»
		«b.generateChannelRef».in?«b.generateInputName»
	«ENDFOR»
	'''
	
	/**
	 * Gets all bindings referenced in expressions within this object.
	 * 
	 * @param it  the object to inspect.
	 * 
	 * @return  all bindings referenced in expressions within it.
	 */
	def getExprBindings(EObject it) {
		EcoreUtil2.eAllOfType(it, BindingExpr).map[source]
	}
}