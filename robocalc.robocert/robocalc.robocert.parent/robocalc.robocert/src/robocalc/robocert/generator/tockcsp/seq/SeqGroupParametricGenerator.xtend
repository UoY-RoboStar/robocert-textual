package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SeqGroupParametricField
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup

/**
 * Generates the parametric part of a sequence group.
 *
 * Sequence groups are in-part parameterised by any assignments made to their
 * target's parameterisation.
 */
class SeqGroupParametricGenerator {
	@Inject extension CSPStructureGenerator
	@Inject extension TargetGenerator
	@Inject extension SubsequenceGenerator
	@Inject extension SeqGroupFieldGenerator
	
	/**
	 * Generates CSP-M for the body of an open form of a sequence group's
	 * parametric part.
	 * 
	 * @param it  the sequence group in question.
	 * 
	 * @return  CSP-M for the parametric module body.
	 */
	def generateParametric(SequenceGroup it) '''
		«timed(generateTargetDef)»
			
		«sequences.generateSequences»
	'''
	
	private def generateTargetDef(SequenceGroup it) '''«SeqGroupParametricField::TARGET.generate» = «target.generate(instantiation)»'''

	private def CharSequence generateSequences(Iterable<Sequence> sequences) '''
		«IF sequences.empty»
			-- No sequences defined in this group
		«ELSE»
			«module(SeqGroupParametricField::SEQUENCE_MODULE.generate, timed(sequences.generateSequencesInner))»
		«ENDIF»
	'''
	
	private def generateSequencesInner(Iterable<Sequence> sequences) '''
		«FOR sequence : sequences SEPARATOR "\n"»
			«sequence.name» =
				«sequence.generate»
		«ENDFOR»
	'''
	
	private def generate(Sequence it) '''
		«body.generate»; -- end of defined steps
		TCHAOS(«MessageSetGenerator::QUALIFIED_UNIVERSE_NAME»)
	'''
}