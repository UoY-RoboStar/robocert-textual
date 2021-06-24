package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to ExtensionalGapMessageSetImpl.
 */
class ExtensionalGapMessageSetImplCustom extends ExtensionalGapMessageSetImpl {
	/**
	 * Extensional gap messages sets are active if non-empty.
	 * 
	 * @return whether there is at least one message in the set.
	 */
	override isActive() {
		!(messages.isNullOrEmpty)
	}
}