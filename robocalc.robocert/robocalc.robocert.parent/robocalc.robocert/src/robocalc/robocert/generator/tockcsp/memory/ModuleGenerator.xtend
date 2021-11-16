package robocalc.robocert.generator.tockcsp.memory

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator
import com.google.inject.Inject
import java.util.List
import java.util.function.Function
import java.util.stream.Collectors
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator
import robocalc.robocert.generator.utils.MemoryFactory.Memory
import robocalc.robocert.generator.utils.MemoryFactory.Memory.Slot
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.generator.intf.core.SpecGroupParametricField
import robocalc.robocert.model.robocert.Binding
import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.generator.utils.name.BindingNamer

/**
 * Generates memory modules for sequences.
 * 
 * The memory definition we use for RoboCert is fairly simplistic, but similar
 * to that used in other RoboStar languages.  For each sequence that needs a
 * memory, we generate a process that offers each binding in the sequence as an
 * in/out channel.  The process is a recursion that continuously offers the
 * current values of each binding through the out channel, while also offering
 * to accept new values (updating its recursion accordingly).
 * 
 * There is no true concurrency in sequence diagrams, so this should be
 * sufficient.
 */
class ModuleGenerator {
	@Inject CTimedGeneratorUtils gu
	@Inject CSPStructureGenerator csp
	@Inject extension TypeGenerator
	@Inject extension BindingNamer

	/**
	 * Generates the module for a memory.
	 * 
	 * @param it  the memory to generate.
	 * 
	 * @return  a CSP-M module containing the memory definition (channels,
	 *          process, and so on).
	 */
	def generate(Memory it) {
		csp.moduleWithPrivate(parent?.name ?: 'MISSING_NAME', generatePrivateBody, generatePublicBody)
	}

	/**
	 * Lifts the CSP-M for a sequence process into a memory context.
	 * 
	 * @param seq      the sequence being lifted.
	 * @param process  its pre-generated process.
	 * 
	 * @return  process, lifted into seq's memory context.
	 */
	def lift(Sequence seq, CharSequence process) {
		csp.function('''«seq.generateMemoryModuleRef»::«LIFT_PROCESS»''', process)
	}

	/**
	 * Gets a reference to the memory channel of binding b.
	 * 
	 * This reference is relative to the parametric sequence group body, so
	 * it assumes that we can traverse backwards through the binding to its
	 * sequence.
	 * 
	 * @param b  the binding for which we want a memory channel.
	 * 
	 * @return  the partially-specified CSP-M memory channel. 
	 */
	def generateChannelRef(Binding b) '''«b.sequence.generateMemoryModuleRef»::«b.unambiguousName»'''

	private def generateMemoryModuleRef(Sequence it) '''«SpecGroupParametricField::MEMORY_MODULE»::«it?.name ?: 'unknown'»'''

	private def getSequence(Binding b) {
		EcoreUtil2.getContainerOfType(b, Sequence)
	}

	private def generatePublicBody(Memory it) '''
		-- Get/set channels
		«generateChannelDefinitions»
		
		«LIFT_PROCESS»(P) = (
			P [| «SYNC_SET» |] «generateInitialRun»
		) \ «SYNC_SET»
	'''

	/**
	 * Generates the channel set for a memory.
	 * 
	 * As mentioned elsewhere, the interface between the memory process and
	 * the definition process is a series of hidden in/out channels, one per
	 * memory slot.  This method generates their CSP-M definitions.
	 */
	private def generateChannelDefinitions(Memory it) '''
		«FOR it : slots SEPARATOR '\n'»
			channel «unambiguousName» {- «binding.name» -} : «MEM_OP_TYPE».«type.compileType»
		«ENDFOR»
	'''

	private def generatePrivateBody(Memory it) '''
		«generateSyncSet»
		
		«generateProcess»
	'''

	private def generateSyncSet(Memory it) {
		csp.definition(SYNC_SET, csp.enumeratedSet(mapOverSlots[unambiguousName]))
	}

	private def generateProcess(Memory it) {
		csp.definition(generateProcessHeader(it), generateProcessBody)
	}

	private def CharSequence generateProcessHeader(Memory it) {
		generateRun[generateHeaderName]
	}

	private def generateInitialRun(Memory it) {
		generateRun[gu.typeDefaultValue(type)]
	}

	/**
	 * Generates a header/invocation of the run process for a memory, using
	 * the given function to transform its slots into arguments.
	 * 
	 * @param it         the memory for which we are generating.
	 * @param transform  the function to apply to slots to produce arguments.
	 * 
	 * @return a header or invocation of the memory's run process, in CSP-M.
	 */
	private def generateRun(Memory it, Function<Memory.Slot, CharSequence> transform) {
		csp.function(RUN_PROCESS, mapOverSlots(transform))
	}

	protected def List<CharSequence> mapOverSlots(Memory it, Function<Slot, CharSequence> transform) {
		slots.stream.map(transform).collect(Collectors::toUnmodifiableList)
	}

	private def generateHeaderName(Memory.Slot it) '''Bnd_«unambiguousName»'''

	private def generateProcessBody(Memory it) '''
		«FOR s : slots SEPARATOR ' [] '»
			(
				«s.generateSlotIn» -> «generateProcessHeader»
			[]
				«s.generateSlotOut» -> «generateProcessHeader»
			)
		«ENDFOR»
	'''

	// Note that these are the reverse direction from how they appear to
	// processes using the memory.  This may seem obvious, but I got it
	// wrong at first (GitHub issue #80 on robocert-sequences...)

	private def generateSlotIn(Memory.Slot it) '''«unambiguousName».set?«generateHeaderName»'''

	private def generateSlotOut(Memory.Slot it) '''«unambiguousName».get!«generateHeaderName»'''

	static val MEM_OP_TYPE = "MemOp" // in robocert_defs
	static val LIFT_PROCESS = "lift"
	static val RUN_PROCESS = "proc"
	static val SYNC_SET = "sync"
}
