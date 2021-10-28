package robocalc.robocert.generator.tockcsp.ll

/**
 * Generates common CSP-M structures such as modules and timed sections.
 * 
 * Use of this class makes uniform indenting and formatting easier.
 */
class CSPStructureGenerator {
	/**
	 * Generates a process/set/function definition.
	 * 
	 * @param head  the header of the item.
	 * @param body  the body of the item.
	 * 
	 * @return  CSP-M for the process.
	 */
	def CharSequence definition(CharSequence head, CharSequence body) '''
		«head» = «body»
	'''

	/**
	 * Generates a CSP function-like construct.
	 * 
	 * @param name  the name of the function/process/module.
	 * @param args  the arguments of the function/process/header.
	 * 
	 * @return  CSP-M for the process header.
	 */
	def CharSequence function(CharSequence name,
		CharSequence ... args) '''«name»«IF !args.empty»«setlike('(', ')', args)»«ENDIF»'''

	/**
	 * Generates a CSP enumerated set.
	 * 
	 * @param args  the contents of the set.
	 * 
	 * @return  CSP-M for the enumerated set.
	 */
	def CharSequence enumeratedSet(CharSequence ... args) {
		setlike('{|', '|}', args)
	}

	def private CharSequence setlike(CharSequence lhs, CharSequence rhs, CharSequence ... args) '''«lhs»
	«FOR arg : args SEPARATOR ',\n'»«arg»«ENDFOR»
«rhs»'''

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
