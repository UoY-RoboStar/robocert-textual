package robocalc.robocert.generator.tockcsp.top

import com.google.inject.Inject
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.CSPFragment
import robocalc.robocert.model.robocert.ProcessCSPFragment
import robocalc.robocert.model.robocert.InlineCSPFragment

/**
 * Generator for CSP fragments.
 */
class CSPFragmentGenerator {
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates CSP-M for a process-level CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(
		ProcessCSPFragment it
	) '''«name» = {- begin CSP fragment -}(
	«contents»
){- end CSP fragment «name» -}'''

	/**
	 * Generates CSP-M for an inline CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(
		InlineCSPFragment it
	) '''{- begin CSP fragment «nameOrFallback» -}
	«contents»
{- end CSP fragment «nameOrFallback» -}'''

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
