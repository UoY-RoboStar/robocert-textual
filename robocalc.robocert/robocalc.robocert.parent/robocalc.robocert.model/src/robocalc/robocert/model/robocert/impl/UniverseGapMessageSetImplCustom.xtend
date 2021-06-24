package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to ExtensionalGapMessageSetImpl.
 */
class UniverseGapMessageSetImplCustom extends UniverseGapMessageSetImpl {
	/**
	 * Universe gap messages sets are always active.
	 * 
	 * @return true.
	 */
	override isActive() {
		true
	}
}