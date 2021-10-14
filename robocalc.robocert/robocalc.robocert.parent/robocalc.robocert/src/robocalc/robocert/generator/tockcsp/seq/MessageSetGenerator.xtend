package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.ExtensionalMessageSet
import com.google.inject.Inject
import robocalc.robocert.model.robocert.UniverseMessageSet
import robocalc.robocert.model.robocert.MessageSet
import robocalc.robocert.model.robocert.RefMessageSet
import robocalc.robocert.model.robocert.NamedMessageSet
import robocalc.robocert.model.robocert.BinaryMessageSet
import robocalc.robocert.model.robocert.BinarySetOperator
import robocalc.robocert.generator.utils.MessageSetOptimiser
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.generator.utils.TargetExtensions

/**
 * CSP generator for message sets.
 * 
 * This generator doesn't optimise, because optimisation throws away
 * containment information.  Optimise the sets in-place before
 * generation.
 */
class MessageSetGenerator {
	@Inject extension TargetExtensions
	@Inject extension MessageSetOptimiser
	@Inject extension MessageSpecGenerator

	/**
	 * Generates a CSP event set for an extensional gap message set.
	 * 
	 * @param it  the sequence gap in question.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch CharSequence generate(ExtensionalMessageSet it) {
		messages.generateBulkCSPEventSet
	}

	/**
	 * Generates a CSP event set for a universe gap message set.
	 * 
	 * @param it  the message set for which we are generating CSP.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch generate(UniverseMessageSet it) {
		QUALIFIED_UNIVERSE_NAME
	}

	/**
	 * Generates a CSP event set for a reference gap message set.
	 * 
	 * @param it  the message set for which we are generating CSP.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch generate(RefMessageSet it) '''«MODULE_NAME»::«set.name»'''

	/**
	 * Generates a CSP event set for a binary gap message set.
	 * 
	 * @param it  the message set.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch CharSequence generate(
		BinaryMessageSet it) '''«operator.generateOp»(«lhs.generate», «rhs.generate»)'''

	/**
	 * Fallback for generating an event set for an unknown gap message set.
	 * 
	 * @param it  the message set.
	 * 
	 * @return generated CSP for the gap message set.
	 */
	def dispatch generate(MessageSet it) '''{- UNKNOWN MESSAGE SET: «it» -} Events'''

	def private generateOp(BinarySetOperator it) {
		switch it {
			case UNION:
				"union"
			case INTERSECTION:
				"inter"
			case DIFFERENCE:
				"diff"
		}
	}

	/**
	 * Generates the named set module for a sequence group.
	 * 
	 * @param sets  the message sets to expose in the module.
	 * @param tgt   the sequence group's target.
	 * 
	 * @return generated CSP for the named message set group.
	 */
	def generateNamedSets(Iterable<NamedMessageSet> sets, Target tgt)
	'''module MsgSets
exports
	«UNIVERSE_NAME» = «tgt.namespace»::«UNIVERSE_DEF_NAME»
	«IF sets !== null»
		«FOR set: sets.filterNull»
			«set.name» = «set.generateNamedSet»
		«ENDFOR»
	«ENDIF»
endmodule'''
	
	def private generateNamedSet(NamedMessageSet it) {
		set = set.optimise
		set.generate
	}
		
	/**
	 * The name of the message set module exposed by RoboCert.
	 */
	public static val MODULE_NAME = "MsgSets"
	
	/**
	 * The name of the universe set exposed by RoboCert in the message set
	 * module.
	 */
	public static val UNIVERSE_NAME = "Universe"
	
	/**
	 * The name of the events set exposed by the RoboChart/RoboSim semantics
	 * and which contains all semantically relevant events.
	 */
	static val UNIVERSE_DEF_NAME = "sem__events"

	/**
	 * The qualified name of the universe set exposed by RoboCert.
	 */
	public static val QUALIFIED_UNIVERSE_NAME = '''«MODULE_NAME»::«UNIVERSE_NAME»'''

}
