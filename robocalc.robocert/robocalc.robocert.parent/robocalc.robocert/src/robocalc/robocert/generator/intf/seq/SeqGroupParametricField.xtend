package robocalc.robocert.generator.intf.seq

/**
 * Enumeration of fields in the parametric part of a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
enum SeqGroupParametricField {
	/**
	 * The module in the sequence group containing any sequences.
	 */
	SEQUENCE_MODULE,
	/**
	 * The process in the sequence group representing the target.
	 */
	TARGET
}