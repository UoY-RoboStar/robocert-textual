package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.ArrowAction

/**
 * Generates CSP event sets for actions.
 * 
 * Such CSP event sets refer to all possible communications an action can
 * accept, and are used to generate gaps.
 */
class ActionCSPEventSetGenerator {
	@Inject extension MessageSpecGenerator
	
	/**
	 * Generates the CSP event set for an arrow action.
	 * @param action  the action for which we are generating the event set.
	 * @return  the event set (less the set delimiters).
	 */
	def dispatch CharSequence generateCSPEventSet(ArrowAction action) {
		action.body.generateCSPEventSet
	}

	/**
	 * Generates the CSP event set for a non-arrow action.
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