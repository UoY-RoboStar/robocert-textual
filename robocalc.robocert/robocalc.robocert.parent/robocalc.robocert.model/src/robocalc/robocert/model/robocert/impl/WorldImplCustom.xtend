package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to WorldImpl.
 */
class WorldImplCustom extends WorldImpl {
	/**
	 * Re-points anySequence to resolve to the world's bidirectional sequence
	 * relation.
	 * 
	 * @return the target's sequence.
	 */
	override basicGetAnySequence() {
		sequence
	}
}