package robocalc.robocert.model.robocert.impl

/**
 * Adds derived operation definitions to ExtensionalMessageSetImpl.
 */
class ExtensionalMessageSetImplCustom extends ExtensionalMessageSetImpl {
	/**
	 * Extensional gap messages sets are active if non-empty.
	 * 
	 * @return whether there is at least one message in the set.
	 */
	override isActive() {
		!(messages.isNullOrEmpty)
	}
}