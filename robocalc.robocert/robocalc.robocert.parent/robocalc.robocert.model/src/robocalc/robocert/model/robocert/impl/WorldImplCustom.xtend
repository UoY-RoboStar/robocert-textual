package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to WorldImpl.
 */
class WorldImplCustom extends WorldImpl {
	/**
	 * Re-points anyGroup to resolve to the world's bidirectional group
	 * relation.
	 * 
	 * @return the target's group.
	 */
	override basicGetAnyGroup() {
		group
	}
	
	/**
	 * @return a human-friendly representation of this world actor.
	 */
	override toString() '''world'''
}