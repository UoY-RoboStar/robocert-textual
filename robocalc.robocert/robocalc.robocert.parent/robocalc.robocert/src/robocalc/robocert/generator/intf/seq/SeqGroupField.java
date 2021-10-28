package robocalc.robocert.generator.intf.seq;

import robocalc.robocert.generator.tockcsp.ll.TickTockContextGenerator;

/**
 * Enumeration of fields in a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
public enum SeqGroupField {
	/**
	 * The closed form of the parametric part of a sequence group definition.
	 */
	PARAMETRIC_CLOSED,
	/**
	 * The open form of the parametric part of a sequence group definition.
	 */
	PARAMETRIC_OPEN,
	/**
	 * The module in the sequence group containing any named
	 * message sets.
	 */
	MESSAGE_SET_MODULE,
	/**
	 * The module instance in the sequence group providing its context for
	 * tick-tock module shifting.
	 */
	TICK_TOCK_CONTEXT;

	@Override
	public String toString() {
		return switch (this) {
			case MESSAGE_SET_MODULE -> "MsgSets";
			case PARAMETRIC_CLOSED -> "Closed";
			case PARAMETRIC_OPEN -> "Open";
			case TICK_TOCK_CONTEXT -> TickTockContextGenerator.CONTEXT_MODULE;
		};
	}
}