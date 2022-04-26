package robocalc.robocert.generator.intf.core;

/**
 * Enumeration of fields in the parametric part of a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
public enum SpecGroupParametricField {

	/**
	 * The module in the specification group containing any memory definitions.
	 */
	MEMORY_MODULE,	
	/**
	 * The module in the specification group containing any sequences.
	 */
	SEQUENCE_MODULE,
	/**
	 * The process in the specification group representing the target.
	 */
	TARGET;

	@Override
	public String toString() {
		return switch(this) {
			case MEMORY_MODULE -> "Memory";
			case SEQUENCE_MODULE -> "Seqs";
			case TARGET -> "Target";
		};
	}
}