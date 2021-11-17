package robocalc.robocert.generator.tockcsp.ll

import java.util.stream.Stream
import java.util.stream.Collectors

/**
 * Generates common CSP-M structures such as modules and timed sections.
 * 
 * Use of this class makes uniform indenting and formatting easier.
 */
class CSPStructureGenerator {
	// This class is in Xtend because it largely abstracts over Xtend's
	// templating engine.
	
	/**
	 * Generates a module instance declaration.
	 * 
	 * @param name  the name of the instance.
	 * @param body  the body of the instance.
	 * 
	 * @return  CSP-M for the instance.
	 */
	def CharSequence instance(CharSequence name, CharSequence body) {
		definition('''instance «name»''', body)
	}
	
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
	
	/**
	 * Lifts a body into a timed section if the given Boolean is true.
	 * 
	 * @param isTimed the boolean.
	 * @param inner   the inner body of the possibly-timed section.
	 * 
	 * @return  CSP-M for the possibly-timed section.
	 */
	def CharSequence timedIf(boolean isTimed, CharSequence inner) {
		isTimed ? timed(inner) : inner
	}
	
	/**
	 * Generates a namespaced concatenation of various naming elements.
	 * 
	 * @param elements the elements to join.
	 * 
	 * @return the namespace-joined string.
	 */
	def CharSequence namespaced(CharSequence... elements) {
		String.join("::", elements)
	}
	
	/**
	 * Generates the bare-bones outer structure of a CSP assertion.
	 * 
	 * @param isNegated  whether the assertion is negated.
	 * @return  CSP-M for the assertion.
	 */
	def CharSequence assertion(boolean isNegated, CharSequence body) '''assert«IF isNegated» not«ENDIF» «body»'''
	
	/**
	 * Generates a refinement with a custom model.
	 * 
	 * @param lhs the left-hand side of the refinement.
	 * @param rhs the right-hand side of the refinement.
	 * @param model the model of the refinement (usually 'T' for traces).
	 * 
	 * @return CSP-M for the refinement.
	 */
	def CharSequence refine(CharSequence lhs, CharSequence rhs, CharSequence model) '''«lhs» [«model»= «rhs»'''

	/**
	 * Appends a tau-priority-tock pragma to the given CSP.
	 * 
	 * @param it the assertion CSP to extend with the pragma.
	 * 
	 * @return the pragma-modified CSP.
	 */
	def CharSequence tauPrioritiseTock(CharSequence it) '''«it» :[tau priority]: {tock}'''
	
	def CharSequence innerJoin(Stream<CharSequence> elements) {
		elements.collect(Collectors.joining("\n"))
	}
}
