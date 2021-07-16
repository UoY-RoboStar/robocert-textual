package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to UniverseMessageSetImpl.
 */
class UniverseMessageSetImplCustom extends UniverseMessageSetImpl {
	/**
	 * Universe gap messages sets are always active.
	 * 
	 * @return true.
	 */
	override isActive() {
		true
	}
	
	/**
	 * Universe gap messages sets are always universal.
	 * 
	 * @return true.
	 */
	override isUniversal() {
		true
	}
}