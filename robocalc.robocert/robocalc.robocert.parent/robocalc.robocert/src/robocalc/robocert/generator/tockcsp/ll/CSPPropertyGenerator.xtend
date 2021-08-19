package robocalc.robocert.generator.tockcsp.ll

import com.google.inject.Inject
import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler
import robocalc.robocert.model.robocert.CSPModel
import robocalc.robocert.model.robocert.CSPProcessSource
import robocalc.robocert.model.robocert.CSPRefinementOperator
import robocalc.robocert.model.robocert.CSPRefinementProperty
import robocalc.robocert.model.robocert.EventSetCSPFragment
import robocalc.robocert.model.robocert.ProcessCSPFragment
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.CSPContextSource
import robocalc.robocert.generator.intf.seq.SequenceLocator

/**
 * Generates CSP assertions.
 */
class CSPPropertyGenerator {
	@Inject extension SequenceLocator
	@Inject extension UnsupportedSubclassHandler

	/**
	 * Generates CSP-M for a refinement property.
	 * 
	 * This is usually just a straightforward expansion of the refinement,
	 * unless the property is an equality, in which case we expand it to
	 * two refinements (one in each direction).
	 * 
	 * @param it  the refinement property in question.
	 * 
	 * @return  CSP-M for the given property.
	 */
	def generateProperty(CSPRefinementProperty it) '''
		«generateRefinement»
		«IF type == CSPRefinementOperator::EQUALS»
			«invert.generateRefinement»
		«ENDIF»
	'''

	/**
	 * @return an inverted copy of the given property.
	 */
	private def invert(CSPRefinementProperty it) {
		EcoreUtil2.copy(it) => [ x |
			x.rhs = lhs
			x.lhs = rhs
		]
	}

	/**
	 * Generates a refinement between two CSP process sources.
	 * 
	 * @param lhs    the left-hand side.
	 * @param rhs    the right-hand side.
	 * @param model  the CSP semantic model.
	 * 
	 * @return  CSP-M for the generated refinement.
	 */
	def generateRefinement(CSPRefinementProperty it) '''
		«generateHeader» «lhs.generateProcess(model)» [T= «rhs.generateProcess(model)»«model.generateTauPriority»
	'''

	private def generateHeader(CSPRefinementProperty it) '''assert«IF isNegated» not«ENDIF»'''

	/**
	 * Generates CSP-M for the process of a process source, potentially lifted
	 * into its tick-tock context.
	 * 
	 * @param it  the process source to generate.
	 * @param m   the target semantic model.
	 * 
	 * @return  CSP-M for the generated process.
	 */
	private def generateProcess(CSPProcessSource it,
		CSPModel m) '''«IF m.tickTock»«generateTickTockContext»::TT(«ENDIF»«generateRawProcess»«IF m.tickTock»)«ENDIF»'''

	private def isTickTock(CSPModel model) {
		model == CSPModel::TICK_TOCK
	}

	// TODO(@MattWindsor91): all of this is provisional
	private def dispatch generateTickTockContext(ProcessCSPFragment it) {
		events?.tickTockContext ?: "{}"
	}

	private def dispatch generateTickTockContext(Sequence it) {
		sequenceMember(CONTEXT_MODULE)
	}

	private def dispatch generateTickTockContext(Target it) {
		getTickTockContext
	}

	private def dispatch generateTickTockContext(CSPProcessSource it) {
		unsupported("CSP process source", "FAIL")
	}

	private def dispatch generateRawProcess(ProcessCSPFragment it) {
		name
	}

	// TODO(@MattWindsor91): unify with Sequence.generateName
	private def dispatch generateRawProcess(Sequence it) {
		fullCSPName
	}

	private def dispatch generateRawProcess(Target it) {
		// TODO(@MattWindsor91): fix instantiations
		targetMember(TARGET_PROCESS)
	}

	private def dispatch generateRawProcess(CSPProcessSource it) {
		unsupported("CSP process source", "FAIL")
	}

	private def dispatch getTickTockContext(EventSetCSPFragment it) '''«name»::«CONTEXT_MODULE»'''

	private def dispatch getTickTockContext(Target it) {
		targetMember(CONTEXT_MODULE)
	}

	private def dispatch getTickTockContext(CSPContextSource it) {
		unsupported("CSP context source", "FAIL")
	}

	// TODO(@MattWindsor91): unify these two in the metamodel?
	private def sequenceMember(Sequence it, CharSequence member) '''«group.name»::«member»'''

	private def targetMember(Target it, CharSequence member) '''«group.name»::«member»'''

	static val TARGET_PROCESS = "Target"
	static val CONTEXT_MODULE = "TTContext"

	/**
	 * @return the appropriate FDR tau priority pragma for this model.
	 */
	private def generateTauPriority(CSPModel it) '''«IF !tickTock» :[tau priority]: {tock}«ENDIF»'''
}
