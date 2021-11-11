/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
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
		-- BEGIN CSP GROUP
		«IF isUntimed»
			«generateInner»
		«ELSE»
			Timed(OneStep) {
				«generateInner»
			}
		«ENDIF»
		-- END CSP GROUP
	'''

	def private generateInner(CSPGroup it) '''
		«FOR f : fragments»
			«f.generate»
		«ENDFOR»
	'''
}
