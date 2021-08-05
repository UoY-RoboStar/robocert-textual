package robocalc.robocert.generator.tockcsp.top

import com.google.inject.Inject
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.CSPFragment
import robocalc.robocert.model.robocert.NamedCSPFragment
import robocalc.robocert.model.robocert.InlineCSPFragment

/**
 * Generator for CSP fragments.
 */
class CSPFragmentGenerator {
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates CSP-M for a named (event set or process) CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(NamedCSPFragment it) '''
		-- begin named CSP fragment
		«name» =
			«contents»
		-- end named CSP fragment «name»
	'''

	/**
	 * Generates CSP-M for an inline CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(InlineCSPFragment it) '''
		-- begin inline CSP fragment «nameOrFallback»
			«contents»
		-- end inline CSP fragment «nameOrFallback»
	'''

	/**
	 * Generates fallback CSP-M for an unrecognised CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(CSPFragment it) {
		unsupported("CSP fragment", "")
	}
	
	def private getNameOrFallback(InlineCSPFragment it) {
		name ?: "(unnamed)"
	}
}
