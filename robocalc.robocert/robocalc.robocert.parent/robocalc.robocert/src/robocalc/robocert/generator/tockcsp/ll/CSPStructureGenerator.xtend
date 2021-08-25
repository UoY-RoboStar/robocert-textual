package robocalc.robocert.generator.tockcsp.ll

/**
 * Generates common CSP-M structures such as modules and timed sections.
 */
class CSPStructureGenerator {
	/**
	 * Generates a CSP-M module with a name and public body.
	 * 
	 * @param name  the name of the module.
	 * @param pub   the public body of the module.
	 * 
	 * @return  CSP-M for the module.
	 */
	def CharSequence module(CharSequence name, CharSequence pub) '''
		module «name»
		exports
			«pub»
		endmodule
	'''

	/**
	 * Generates a CSP-M module with a name and both public and private bodies.
	 * 
	 * @param name  the name of the module.
	 * @param priv  the private body of the module.
	 * @param pub   the public body of the module.
	 * 
	 * @return  CSP-M for the module.
	 */
	def CharSequence moduleWithPrivate(CharSequence name, CharSequence priv, CharSequence pub) '''
		module «name»
			«priv»
		exports
			«pub»
		endmodule
	'''
	
	/**
	 * Generates a timed section with the appropriate timing function.
	 * 
	 * @param inner  the inner body of the timed section.
	 * 
	 * @return  CSP-M for the timed section.
	 */
	def CharSequence timed(CharSequence inner) '''
		Timed(OneStep) {
			«inner»
		}
	'''
}