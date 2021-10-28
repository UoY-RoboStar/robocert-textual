package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.generator.utils.MemoryFactory

/**
 * Generates sequences.
 */
class SequenceGenerator {
	@Inject extension MemoryFactory
	@Inject extension SubsequenceGenerator
	@Inject MemoryGenerator mg
	
	/**
	 * Generates CSP-M for a sequence.
	 * 
	 * If the sequence needs a memory, it will be lifted into the appropriate
	 * context.
	 * 
	 * @param it  the sequence for which we are generating CSP-M.
	 * 
	 * @return  the generated CSP-M for the sequence.
	 */	
	def generate(Sequence it) {
		hasMemory ? mg.lift(it, generateInner) : '''(
	«generateInner»
)'''
	}
		
	private def generateInner(Sequence it) '''
		«body.generate»; -- end of defined steps
		TCHAOS(«MessageSetGenerator::QUALIFIED_UNIVERSE_NAME»)
	'''
}