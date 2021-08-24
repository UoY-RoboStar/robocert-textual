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

	/**
	 * Extensional gap messages sets are never universal.
	 * 
	 * Technically, they could be if they enumerated every possible message in
	 * the context, but isUniversal is allowed to be pessimistic and so we
	 * don't check that.
	 * 
	 * @return false.
	 */
	override isUniversal() {
		false
	}
}
