package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.utils.MemoryFactory.Memory
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator

/**
 * Generates memory channels and processes for sequences.
 */
class MemoryGenerator {
	@Inject extension CSPStructureGenerator
	@Inject extension TypeGenerator
	
	def generate(Memory it) {
		module(parent?.name ?: 'MISSING_NAME', generateChannelSet)
	}
	
	private def generateChannelSet(Memory it) '''
		«FOR s: slots SEPARATOR '\n'»
			«s.generateChannel»
		«ENDFOR»
	'''
		
	private def generateChannel(Memory.Slot it) '''
		channel «unambiguousName» {- «binding.name» -} : InOut -> «type.compileType»
	'''
}