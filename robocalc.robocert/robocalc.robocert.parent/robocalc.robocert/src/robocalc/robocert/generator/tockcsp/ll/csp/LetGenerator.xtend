package robocalc.robocert.generator.tockcsp.ll.csp

/**
 * Handles production of CSP-M let-within code.
 * 
 * The usual way in which this class will be used is through calls of the form
 * 'this.let(def1, def2, def3).within(body)'.
 * 
 * @author Matt Windsor
 */
class LetGenerator {
	/**
	 * Starts a let-within definition with the given elements.
	 * 
	 * @param elements the elements to have between 'let' and 'within'.
	 * 
	 * @return an object that can be finished with a 'within' call.
	 */
	def Let let(CharSequence... elements) {
		return new Let(elements)
	}

	/**
	 * Helper class for producing let-within CSP.
	 */
	static class Let {
		CharSequence[] elements
		 
		new (CharSequence... elements) {
			this.elements = elements
		}
		
		/**
		 * Finishes a let-within definition.
		 * 
		 * @param body the 'within' part of the body.
		 * 
		 * @return the finished let-within sequence.
		 */
		def CharSequence within(CharSequence body) '''
		«IF elements.empty»
			«body»
		«ELSE»
			let
				«FOR element : elements»
					«element»
				«ENDFOR»
			within
				«body»
		«ENDIF»'''
	}
}