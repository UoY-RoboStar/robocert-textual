package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to RCModuleTargetImpl.
 */
class RCModuleTargetImplCustom extends RCModuleTargetImpl {
	/**
	 * Re-points anySequence to resolve to the target's bidirectional sequence
	 * relation.
	 * 
	 * @return the target's sequence.
	 */
	override basicGetAnySequence() {
		sequence
	}
}