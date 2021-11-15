package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.generator.utils.TargetExtensions
import robocalc.robocert.model.robocert.Instantiation
import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SeqGroupField
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator

/**
 * Generator for sequence groups.
 * 
 * This generator handles the top-level, unparametrised parts of a sequence group.
 * 
 * @see SeqGroupParametricGenerator
 */
class SeqGroupGenerator {
	@Inject CSPStructureGenerator csp

	@Inject extension TargetGenerator
	@Inject extension TargetExtensions
	@Inject extension MessageSetGenerator
	@Inject extension SeqGroupParametricGenerator

	/**
	 * Generates CSP for a sequence group.
	 * 
	 * @param it  the sequence group for which we are generating CSP.
	 * 
	 * @return CSP for the sequence group.
	 */
	def CharSequence generate(SequenceGroup it) '''
		-- SEQUENCE GROUP (target «target»)
		«csp.moduleWithPrivate(name, generatePrivateDefs, generatePublicDefs)»
	'''

	private def generatePrivateDefs(SequenceGroup it) '''
		«messageSets.generateNamedSets(target)»
	'''

	/**
	 * Generates the public definitions for a sequence group module.
	 * 
	 * These include the tick-tock context (which must be exposed for any
	 * tick-tock properties over the group's sequences and target to go
	 * through), and the constant-specific submodules.
	 * 
	 * There are two such modules: an 'open' one, which holds all of the
	 * parameters of the group open;
	 * and a 'closed' one, which instantiates them all to defaults.
	 * 
	 * @param it  the sequence group for which we are generating definitions.
	 * 
	 * @return  the generated CSP-M.
	 */
	private def generatePublicDefs(SequenceGroup it) '''
		«generateTickTockContext»

		«generateOpenDef»
		«generateClosedDef»
	'''

	/**
	 * Generates a process definition for the 'closed' form of this group.
	 * 
	 * The closed form has no parameters, with all constants assigned values
	 * either from its target's instantiation or from
	 * the top-level instantiations.csp file.
	 * 
	 * @param it  the group for which we are generating a closed form.
	 * 
	 * @return CSP defining the 'closed' form of this group.
	 */
	def CharSequence generateClosedDef(SequenceGroup it) '''
		instance «SeqGroupField::PARAMETRIC_CLOSED.toString» =
			«generateOpenSig(instantiation)»
	'''

	/**
	 * Generates an external reference for the 'open' form of this group.
	 * 
	 * The open form has parameters exposed, and any reference to it must
	 * fill those parameters using either values in the given instantiation or,
	 * where values are missing, references to the instantiations CSP file.
	 * 
	 * @param it  the group for which we are generating CSP.
	 * 
	 * @return generated CSP for referring to the 'open' form of this group.
	 */
	def CharSequence generateOpenRef(SequenceGroup it, Instantiation instantiation) '''
		«name»::«generateOpenSig(instantiation)»
	'''

	/**
	 * Generates a process definition for the 'open' form of this target.
	 * 
	 * @param it             the group for which we are generating an open
	 *                       form.
	 * @param instantiation  the instantiation (may be null).
	 * 
	 * @return generated CSP for the 'open' form of a sequence's target.
	 */
	def CharSequence generateOpenDef(SequenceGroup it) {
		csp.module(generateOpenSig(null), generateParametric)
	}

	private def generateTickTockContext() '''instance «SeqGroupField::TICK_TOCK_CONTEXT.toString» = model_shifting(«MessageSetGenerator::QUALIFIED_UNIVERSE_NAME»)'''

	/**
	 * Generates the signature of an open sequence group definition or reference.
	 * 
	 * Because the parameters used in the definition are just the constant IDs,
	 * which are also how we refer to any fallback references to the
	 * instantiations file, both definitions and references can have the same
	 * signature generator.
	 * 
	 * @param it         the group for which we are generating an open form.
	 * @param outerInst  any instantiation being applied at the outer level
	 *                   (may be null).
	 * 
	 * @return CSP referring to, or giving the signature of, the 'open' form of
	 *         this group.
	 */
	private def generateOpenSig(
		SequenceGroup it,
		Instantiation outerInst
	) {
		csp.function(SeqGroupField::PARAMETRIC_OPEN.toString, generateOpenSigParams(outerInst))
	}

	private def generateOpenSigParams(SequenceGroup it, Instantiation outerInst) {
		uninstantiatedConstants.map[outerInst.generateConstant(it)]
	}

	private def uninstantiatedConstants(SequenceGroup it) {
		target?.uninstantiatedConstants(instantiation)?.toIterable
	}

}
