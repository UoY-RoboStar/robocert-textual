package robocalc.robocert.generator.tockcsp.ll

import robocalc.robocert.model.robocert.ProcessCSPFragment
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.CSPContextSource
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import com.google.inject.Inject
import robocalc.robocert.model.robocert.EventSetCSPFragment
import robocalc.robocert.generator.tockcsp.core.SpecGroupElementFinder
import robocalc.robocert.generator.intf.core.SpecGroupField
import robocalc.robocert.model.robocert.SpecGroup

/**
 * Generates the appropriate tick-tock 'context' (minimal covering set of all
 * events in a process) for use in model-shifting tick-tock refinement to
 * traces refinement.
 * 
 * See: J. Baxter, P. Ribeiro, A. Cavalcanti: Sound reasoning in tock-CSP.
 *      Acta Informatica. (2021).
 */
class TickTockContextGenerator {
	@Inject extension SpecGroupElementFinder
	@Inject extension UnsupportedSubclassHandler
	
	/**
	 * Generates the appropriate tick-tock context from a process fragment.
	 *
	 * This delegates to the process's attached context source.
	 *
	 * @param it  the process fragment whose context is required.
	 * 
	 * @return  the appropriate context source.
	 */
	def dispatch CharSequence generateTickTockContext(ProcessCSPFragment it) {
		events?.generateTickTockContext ?: EMPTY_SET
	}

	/**
	 * Generates the appropriate tick-tock context from a sequence.
	 *
	 * A sequence's context is that attached to its parent group.
	 *
	 * @param it  the sequence whose context is required.
	 * 
	 * @return  the appropriate context source.
	 */
	def dispatch generateTickTockContext(Sequence it) {
		group.generateGroupTickTockContext
	}

	/**
	 * Generates the appropriate tick-tock context from a target.
	 *
	 * A target's context is that attached to its parent group.
	 *
	 * @param it  the target whose context is required.
	 * 
	 * @return  the appropriate context source.
	 */
	def dispatch generateTickTockContext(Target it) {
		group.generateGroupTickTockContext
	}

	/**
	 * Generates the appropriate tick-tock context from an event set.
	 *
	 * @param it  the target whose context is required.
	 * 
	 * @return  the appropriate context source.
	 */	
	def dispatch generateTickTockContext(EventSetCSPFragment it) '''«name»::«CONTEXT_MODULE»'''
	
	private def CharSequence generateGroupTickTockContext(SpecGroup it) {
		getFullCSPName(SpecGroupField::TICK_TOCK_CONTEXT)
	}


	def dispatch generateTickTockContext(CSPContextSource it) {
		unsupported("CSP process source", EMPTY_SET)
	}
	
	/**
	 * Standard name for context modules.
	 */
	public static val CONTEXT_MODULE = "TTContext"

	static val EMPTY_SET = "{}"
}