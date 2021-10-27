package robocalc.robocert.generator.intf.seq

/**
 * Enumeration of fields in a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
enum SeqGroupField {
	/**
	 * The module in the sequence group containing any memory definitions.
	 */
	MEMORY_MODULE,	
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
	TICK_TOCK_CONTEXT
}