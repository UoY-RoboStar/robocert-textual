package robocalc.robocert.generator.tockcsp.ll

import robocalc.robocert.model.robocert.CSPGroup
import com.google.inject.Inject

/**
 * Generator for CSP fragment groups.
 */
class CSPGroupGenerator {
	// TODO(@MattWindsor91): part-merge with sequence group generation?
	// TODO(@MattWindsor91): allow modularisation?
	@Inject extension CSPFragmentGenerator

	/**
	 * Generates CSP-M for a CSP group.
	 * 
	 * This will wrap the group in a timed section if needed.
	 * 
	 * @param it  the CSP group to generate.
	 * 
	 * @return  the generated CSP-M.
	 */
	def generate(CSPGroup it) '''
		«IF isUntimed»
			«generateInner»
		«ELSE»
			Timed(OneStep) {
				«generateInner»
			}
		«ENDIF»
	'''

	def private generateInner(CSPGroup it) '''
		«FOR f : fragments»
			«f.generate»
		«ENDFOR»
	'''
}
