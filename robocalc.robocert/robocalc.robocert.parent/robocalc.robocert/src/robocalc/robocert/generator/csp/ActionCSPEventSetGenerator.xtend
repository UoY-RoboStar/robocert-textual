package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.ArrowAction
import java.util.Iterator
import java.util.Collections
import com.google.common.collect.Iterators

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
	 * @return  the event set as an iterator of generated elements.
	 */
	def dispatch Iterator<CharSequence> generateCSPEventSet(ArrowAction action) {
		Iterators.singletonIterator(action.body.generateCSPEventSet)
	}

	/**
	 * Generates the CSP event set for a non-arrow action.
	 * @param action  the action for which we are generating the event set.
	 * @return  nothing (empty set).
	 */
	def dispatch Iterator<CharSequence> generateCSPEventSet(SequenceAction action) {
		Collections.emptyIterator
	}

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