package robocalc.robocert.generator.utils

import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.ExtensionalGapMessageSet
import robocalc.robocert.model.robocert.UniverseGapMessageSet
import robocalc.robocert.model.robocert.GapMessageSet

/**
 * Extension methods for handling gaps.
 */
class GapExtensions {
	// TODO(@MattWindsor91): fold some or all of these into the metamodel.
		
	/**
	 * Does this sequence gap allow messages to occur?
	 * 
	 * @param it  the sequence gap in question.
	 * 
	 * @return true if, and only if, there is at least one message in the
	 *         allows set and the forbidden set is not the universe.
	 */
	def boolean allowsMessages(SequenceGap it) {
		allowed.hasMessages && !forbidden.isUniverse
	}

	/**
	 * @return whether this message set has messages.
	 */
	def dispatch boolean hasMessages(ExtensionalGapMessageSet set) {
		// TODO: check if this is actually necessary
		// TODO: move to metamodel?		
		!(set?.messages.isNullOrEmpty)
	}

	/**
	 * @return whether this message set has messages.
	 */
	def dispatch boolean hasMessages(UniverseGapMessageSet set) {
		true
	}

	/**
	 * @return whether this message set has messages.
	 */
	def dispatch boolean hasMessages(GapMessageSet set) {
		false
	}

	/**
	 * @return whether this message set contains every message.
	 */
	def dispatch boolean isUniverse(UniverseGapMessageSet set) {
		true
	}

	/**
	 * @return whether this message set contains every message.
	 */
	def dispatch boolean isUniverse(GapMessageSet set) {
		false
	}	
}