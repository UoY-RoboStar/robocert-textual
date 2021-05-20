package robocalc.robocert.generator

/**
 * Directions of arrows in terms of the CSP semantics.
 * 
 * This may eventually get replaced with either something more relevant, or
 * a reference to the RoboChart metamodel.
 */
enum ArrowDirection {
	/**
	 * Unknown direction (probably a validation error).
	 */
	Unknown,
	/**
	 * An input (to the module, from the platform).
	 */
	Input,
	/**
	 * An output (from the module, to the platform).
	 */
	Output
}