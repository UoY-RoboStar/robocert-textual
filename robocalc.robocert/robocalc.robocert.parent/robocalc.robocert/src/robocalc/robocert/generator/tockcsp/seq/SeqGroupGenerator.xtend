package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.generator.utils.TargetExtensions
import robocalc.robocert.model.robocert.Instantiation
import com.google.inject.Inject
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.generator.intf.seq.SeqGroupField

/**
 * Generator for sequence groups.
 */
class SeqGroupGenerator {
	@Inject extension TargetGenerator
	@Inject extension TargetExtensions
	@Inject extension SubsequenceGenerator
	@Inject extension MessageSetGenerator
	@Inject extension SeqGroupFieldGenerator
	
	/**
	 * Generates CSP for a sequence group.
	 * 
	 * @param it  the sequence group for which we are generating CSP.
	 * 
	 * @return CSP for the sequence group.
	 */
	def CharSequence generate(SequenceGroup it) '''
		-- SEQUENCE GROUP
		-- from: «target»
		-- to:   «world»
		«module(name, generateDefs)»
	'''
	

	
	// There are two submodules inside a sequence group:
	//
	// - an 'open' one, which holds all of the parameters of the group open;
	// - a 'closed' one, which instantiates them all to defaults.
	private def generateDefs(SequenceGroup it) '''
		«generateOpenDef»
		«generateClosedDef»
	'''

	/**
	 * Generates an external reference for the 'closed' form of this sequence group.
	 * 
	 * The closed form has no parameters, with all constants assigned values
	 * either from its target's instantiation or from
	 * the top-level instantiations.csp file.
	 * 
	 * @param it  the group for which we are generating a closed form.
	 * 
	 * @return CSP referencing the 'closed' form of this group.
	 */
	def CharSequence generateClosedRef(SequenceGroup it) '''«name»::«CLOSED_DEF_MODULE_NAME»'''

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
		instance «CLOSED_DEF_MODULE_NAME» =
			«generateOpenSig(instantiation)»
	'''
	
	/**
	 * The name of the module inside a sequence group that contains the
	 * closed definition.
	 */
	package static val CLOSED_DEF_MODULE_NAME = '''Instance'''

	package static val TARGET_DEF_NAME = '''Target'''


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
	def CharSequence generateOpenDef(SequenceGroup it) '''
		module «generateOpenSig(null)»
			«messageSets.generateNamedSets(target)»
		exports
			«generateTickTockContext»
		
			«timed(generateTargetDef)»
			
			«sequences.generateSequences»
		endmodule
	'''
	
	private def generateTickTockContext() '''instance «SeqGroupField::TICK_TOCK_CONTEXT.generate» = model_shifting(«MessageSetGenerator::QUALIFIED_UNIVERSE_NAME»)'''


	private def generateTargetDef(SequenceGroup it) '''«TARGET_DEF_NAME» = «target.generate(instantiation)»'''

	private def CharSequence generateSequences(Iterable<Sequence> sequences) '''
		«IF sequences.empty»
			-- No sequences defined in this group
		«ELSE»
			«module(SeqGroupField::SEQUENCE_MODULE.generate, timed(sequences.generateSequencesInner))»
		«ENDIF»
	'''
	
	private def generateSequencesInner(Iterable<Sequence> sequences) '''
		«FOR sequence : sequences SEPARATOR "\n"»
			«sequence.name» =
				«sequence.body.generate»
		«ENDFOR»
	'''
	
	/**
	 * Generates a CSP-M module with a name and public body.
	 * 
	 * @param name  the name of the module.
	 * @param pub   the public body of the module.
	 * 
	 * @return  CSP-M for the module.
	 */
	private def CharSequence module(CharSequence name, CharSequence pub) '''
		module «name»
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
	private def CharSequence timed(CharSequence inner) '''
		Timed(OneStep) {
			«inner»
		}
	'''
	

	/**
	 * Generates the signature of an open sequence group definition or reference.
	 * 
	 * Because the parameters used in the definition are just the constant IDs,
	 * which are also how we refer to any fallback references to the
	 * instantiations file, both definitions and references can have the same
	 * signature generator.
	 * 
	 * @param it         the group for which we are generating an open form.
	 * @param outerInst  any instantiation being applied at the  (may be null).
	 * 
	 * @return CSP referring to, or giving the signature of, the 'open' form of
	 *         this group.
	 */
	private def generateOpenSig(SequenceGroup it, Instantiation outerInst) '''
	Open«FOR c : uninstantiatedConstants BEFORE '(' SEPARATOR ',' AFTER ')'»
			«outerInst.generateConstant(c)»
	«ENDFOR»'''
	
	private def uninstantiatedConstants(SequenceGroup it) {
		target?.uninstantiatedConstants(instantiation)?.toIterable
	}

}