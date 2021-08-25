package robocalc.robocert.generator.intf.seq

/**
 * Enumeration of fields in a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
enum SeqGroupField {
	/**
	 * The module in the sequence group containing any named
	 * message sets.
	 */
	MESSAGE_SET_MODULE,
	/**
	 * The module in the sequence group containing any sequences.
	 */
	SEQUENCE_MODULE,
	/**
	 * The process in the sequence group representing the target.
	 */
	TARGET,
	/**
	 * The module instance in the sequence group providing its context for
	 * tick-tock module shifting.
	 */
	TICK_TOCK_CONTEXT
}