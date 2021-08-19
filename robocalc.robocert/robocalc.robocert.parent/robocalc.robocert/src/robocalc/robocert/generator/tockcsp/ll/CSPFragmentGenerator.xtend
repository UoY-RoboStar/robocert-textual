package robocalc.robocert.generator.tockcsp.ll

import com.google.inject.Inject
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.CSPFragment
import robocalc.robocert.model.robocert.NamedCSPFragment
import robocalc.robocert.model.robocert.InlineCSPFragment
import robocalc.robocert.model.robocert.EventSetCSPFragment

/**
 * Generator for CSP fragments.
 */
class CSPFragmentGenerator {
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates CSP-M for an event set CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(EventSetCSPFragment it) '''
		module «name»
		exports
			Set = «contents»
			
			instance TTContext = model_shifting(Set)
		endmodule
	'''

	/**
	 * Generates CSP-M for an otherwise non-special-cased named CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(NamedCSPFragment it) '''
		«name» =
			«contents»
	'''

	/**
	 * Generates CSP-M for an inline CSP fragment.
	 * 
	 * @param it  the fragment to generate.
	 * 
	 * @return the generated CSP-M.
	 */
	def dispatch generate(InlineCSPFragment it) {
		contents
	}

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
}
