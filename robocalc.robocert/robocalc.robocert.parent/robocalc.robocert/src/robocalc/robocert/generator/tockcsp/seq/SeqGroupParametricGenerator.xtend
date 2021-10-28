package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SeqGroupParametricField
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
	@Inject CSPStructureGenerator csp
	@Inject extension TargetGenerator
	@Inject extension SequenceGenerator
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
		«generateTargetDef»
		«IF !sequences.empty»
		
		«sequences.generateSequences»
		«ENDIF»
	'''
	
	private def generateTargetDef(SequenceGroup it) {
		csp.timed(
			csp.definition(
				SeqGroupParametricField::TARGET.generate,
				target.generate(instantiation)
			)
		)
	}

	private def CharSequence generateSequences(Iterable<Sequence> sequences) {
		csp.module(SeqGroupParametricField::SEQUENCE_MODULE.generate, csp.timed(sequences.generateSequencesInner))
	}
	
	private def generateSequencesInner(Iterable<Sequence> sequences) '''
		«FOR sequence : sequences SEPARATOR "\n"»
			«csp.definition(sequence.name, sequence.generate)»
		«ENDFOR»
	'''
}