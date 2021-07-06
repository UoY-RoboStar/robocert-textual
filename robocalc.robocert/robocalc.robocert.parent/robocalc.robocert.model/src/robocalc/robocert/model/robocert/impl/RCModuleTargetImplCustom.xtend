package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to RCModuleTargetImpl.
 */
class RCModuleTargetImplCustom extends RCModuleTargetImpl {
	/**
	 * Re-points anyGroup to resolve to the target's bidirectional group
	 * relation.
	 * 
	 * @return the target's group.
	 */
	override basicGetAnyGroup() {
		group
	}
}