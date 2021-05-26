package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.FinalAction

/**
 * Top-level CSP generator for sequence actions.
 */
class ActionGenerator {
	@Inject extension MessageSpecGenerator mg

	/**
	 * Generates CSP for an arrow action.
	 * 
	 * @param action  the arrow action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(ArrowAction action) '''«action.body.generatePrefix» -> SKIP'''

	/**
	 * Generates CSP for a final action.
	 * 
	 * @param action  the final action.
	 * @param gap     the gap.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(FinalAction action) '''STOP''' // TODO: decide whether this should be 'STOP' or 'SKIP'

	/**
	 * Generates fallback CSP for an unsupported action.
	 * 
	 * @param action  the action.
	 * 
	 * @return the generated CSP.
	 */
	def dispatch generate(SequenceAction action) '''{- unsupported action: «action» -} STOP'''

	/**
	 * Generates the CSP event set for an arrow action.
	 * @param action  the action for which we are generating the event set.
	 * @return  the event set (less the set delimiters).
	 */
	def dispatch CharSequence generateCSPEventSet(ArrowAction action) {
		action.body.generateCSPEventSet
	}

	/**
	 * Generates the CSP event set for a non-arrow action..
	 * @param action  the action for which we are generating the event set.
	 * @return  nothing (empty set).
	 */
	def dispatch CharSequence generateCSPEventSet(SequenceAction action) ''''''

	// TODO: these extension methods should either move into the metamodel, or into a utilities class. 
	/**
	 * Asks whether an arrow action has CSP events.
	 * @param action  the action to investigate.
	 * @return  true.
	 */
	def dispatch hasCSPEvents(ArrowAction action) {
		true
	}

	/**
	 * Asks whether a non-arrow action has CSP events.
	 * @param action  the action to investigate.
	 * @return  false.
	 */
	def dispatch hasCSPEvents(SequenceAction action) {
		false
	}

}
