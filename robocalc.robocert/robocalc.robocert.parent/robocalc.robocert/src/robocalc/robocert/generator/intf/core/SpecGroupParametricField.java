package robocalc.robocert.generator.intf.core;

/**
 * Enumeration of fields in the parametric part of a sequence group.
 * 
 * These fields currently correspond directly to tock-CSP subdefinitions, but
 * this may eventually change.
 */
public enum SpecGroupParametricField {
	/**
	 * The enumeration of actors in the specification group.
	 */
	ACTOR_ENUM,

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
			case ACTOR_ENUM -> "Actors";
			case MEMORY_MODULE -> "Memory";
			case SEQUENCE_MODULE -> "Seqs";
			case TARGET -> "Target";
		};
	}
}