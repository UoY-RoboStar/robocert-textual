package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SeqGroupParametricField
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.generator.utils.MemoryFactory
import java.util.stream.Collectors
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator

/**
 * Generates the parametric part of a sequence group.
 *
 * Sequence groups are in-part parameterised by any assignments made to their
 * target's parameterisation.
 */
class SeqGroupParametricGenerator {
	@Inject CSPStructureGenerator csp
	
	@Inject extension ModuleGenerator
	@Inject extension MemoryFactory
	@Inject extension TargetGenerator
	@Inject extension SequenceGenerator
	
	/**
	 * Generates CSP-M for the body of the parametric part of a sequence group.
	 * 
	 * This part contains everything dependent on the definition of constants,
	 * as well as anything that FDR requires to be in the same module.
	 * 
	 * @param it  the sequence group in question.
	 * 
	 * @return  CSP-M for the parametric module body.
	 */
	def generateParametric(SequenceGroup it) '''
		«generateTargetDef»
		
		«IF sequences.empty»
			-- No sequences defined
		«ELSE»
			«sequences.stream.buildMemories.collect(Collectors.toList).generateMemories»

			«sequences.generateSequences»
		«ENDIF»
	'''
	
	/**
	 * Generates the memory set for this 
	 */
	private def CharSequence generateMemories(Iterable<MemoryFactory.Memory> memories) '''
		«IF memories.empty»
			-- No memories defined in this group
		«ELSE»
			«csp.module(SeqGroupParametricField::MEMORY_MODULE.toString, memories.generateMemoriesInner)»
		«ENDIF»
	'''

	private def generateMemoriesInner(Iterable<MemoryFactory.Memory> memories) '''
		«FOR m : memories SEPARATOR '\n'»
			«m.generate»
		«ENDFOR»
	'''
	
	private def generateTargetDef(SequenceGroup it) {
		csp.timed(
			csp.definition(
				SeqGroupParametricField::TARGET.toString,
				target.generate(instantiation)
			)
		)
	}

	private def CharSequence generateSequences(Iterable<Sequence> sequences) {
		csp.module(SeqGroupParametricField::SEQUENCE_MODULE.toString, csp.timed(sequences.generateSequencesInner))
	}
	
	private def generateSequencesInner(Iterable<Sequence> sequences) '''
		«FOR sequence : sequences SEPARATOR "\n"»
			«csp.definition(sequence.name, sequence.generate)»
		«ENDFOR»
	'''
}