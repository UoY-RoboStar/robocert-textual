package robocalc.robocert.generator.tockcsp.memory

import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.model.robocert.BindingExpr
import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import robocalc.robocert.model.robocert.Binding
import robocalc.robocert.generator.tockcsp.top.BindingGenerator

/**
 * Generates loads and stores between local storage and the memory module.
 * 
 * Loads are used to put bindings into scope at the CSP level before they
 * appear in expressions.  Stores are used to update the memory with
 * bindings captured in arrow actions.
 */
class LoadStoreGenerator {
	@Inject extension BindingGenerator
	@Inject extension ModuleGenerator

	/**
	 * Generates a memory load prefix for all bindings referenced in
	 * child expressions of this object.
	 * 
	 * This should be inserted before the main CSP artefact generated from the
	 * object.
	 * 
	 * @param it  the object whose memory loads we are generating.
	 * 
	 * @return  the CSP-M prefix.
	 */
	def generateExpressionLoads(EObject it) {
		exprBindings.generateLoads
	}
	
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
	def generateLoads(Binding ...bindings)
	'''«FOR b: bindings SEPARATOR '->' AFTER ' -> '»«b.generateChannelRef».in?«b.generateInputName»«ENDFOR»'''
	
	/**
	 * Generates a memory store prefix for all bindings included in
	 * this object.
	 * 
	 * This should be inserted AFTER the CSP prefix that produced the bindings
	 * (between the '->' and the 'SKIP').
	 * 
	 * @param it  the object whose memory stores we are generating.
	 * 
	 * @return  the CSP-M prefix.
	 */
	def generateBindingStores(EObject it) {
		EcoreUtil2.eAllOfType(it, Binding).generateStores
	}
	
	/**
	 * Generates a memory store prefix for a set of bindings.
	 * 
	 * This should be inserted AFTER the CSP prefix that produced the bindings
	 * (between the '->' and the 'SKIP').
	 * 
	 * @param bindings  the bindings from which we are generating.
	 * 
	 * @return  the CSP-M prefix.
	 */
	def generateStores(Binding ...bindings)
	'''«FOR b: bindings SEPARATOR '->' AFTER ' -> '»«b.generateChannelRef».out.«b.generateExpressionName»«ENDFOR»'''
	
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