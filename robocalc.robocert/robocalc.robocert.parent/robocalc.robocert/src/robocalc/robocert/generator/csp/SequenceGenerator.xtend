package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Sequence
import com.google.inject.Inject
import robocalc.robocert.model.robocert.Subsequence
import robocalc.robocert.model.robocert.SequenceGroup

/**
 * A generator that emits CSP for sequences and subsequences.
 */
class SequenceGenerator implements SubsequenceGenerator {
	@Inject extension StepGenerator
	@Inject extension MessageSetGenerator
	@Inject extension TargetGenerator

	/**
	 * Generates CSP for a sequence group.
	 * 
	 * @param it  the sequence group for which we are generating CSP.
	 * 
	 * @return CSP for the sequence group.
	 */
	def CharSequence generateGroup(SequenceGroup it) '''
		module «name»
			-- target «target»
			-- world «world»
			
			«messageSets.generateNamedSets(target)»

		exports
			Timed(OneStep) {
				«target.generateOpenTargetDef»
				«target.generateClosedTargetDef»
			}
			
			«sequences.generateSequences»
		endmodule
	'''

	private def CharSequence generateSequences(Iterable<Sequence> sequences) '''
		«IF sequences.empty»
			-- No sequences defined in this group
		«ELSE»
			module Sequences
			exports
				Timed(OneStep) {
					«FOR sequence : sequences»
						«sequence.name» =
							«sequence.body.generate»
					«ENDFOR»
				}
			endmodule
		«ENDIF»
	'''
	
	/**
	 * Generates CSP for a subsequence.
	 * 
	 * @param it  the step set for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	override CharSequence generate(Subsequence it) '''
		«FOR step : steps SEPARATOR ';'»
			«step.generate»
		«ENDFOR»
	'''

	/**
	 * Generates a fully qualified reference to a sequence.
	 * 
	 * @param it  the sequence for which we are generating a reference.
	 * 
	 * @return a qualified name for it.
	 */
	def CharSequence generateName(Sequence it)
		'''«group.name»::Sequences::«name»'''


}
