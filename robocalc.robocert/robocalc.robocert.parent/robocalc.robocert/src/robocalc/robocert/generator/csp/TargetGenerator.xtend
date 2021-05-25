package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.ModuleActor
import robocalc.robocert.model.robocert.TargetActor

/**
 * Generates CSP referring to a target.
 */
class TargetGenerator {	
	/**
	 * @return generated CSP for a module sequence target.
	 * 
	 * @param tgt  the context for which we are generating CSP.
	 */
	def dispatch CharSequence generate(ModuleActor tgt)
		// TODO: move this and the import logic into the same place.
		
		// TODO: ideally this should get constant information from the
		// RoboChart metamodel, and inject user-defined values in.
		// Presumably the constant overriding should be per-assertion.
		'''P_«tgt.module.name»'''

	def dispatch CharSequence generate(TargetActor tgt)
		'''{- UNSUPPORTED TARGET: «tgt» -} STOP'''
}