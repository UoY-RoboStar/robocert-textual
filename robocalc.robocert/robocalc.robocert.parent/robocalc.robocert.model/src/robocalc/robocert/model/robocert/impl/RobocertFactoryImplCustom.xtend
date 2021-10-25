package robocalc.robocert.model.robocert.impl

/**
 * Custom factory that injects 'custom' versions of various RoboCert EClasses,
 * including implementations of derived elements.
 */
class RoboCertFactoryImplCustom extends RoboCertFactoryImpl {
	// Actors need custom impls to inject their anySequence definition.

	/**
	 * @return a custom form of RCModuleTargetImpl.
	 */
	override createRCModuleTarget() {
		new RCModuleTargetImplCustom
	}
	
	/**
	 * @return a custom form of WorldImpl.
	 */
	override createWorld() {
		new WorldImplCustom
	}

	/**
	 * @return a custom form of SequenceGroupImpl.
	 */
	override createSequenceGroup() {
		new SequenceGroupImplCustom
	}
	
	/**
	 * @return a custom form of SequenceImpl.
	 */
	override createSequence() {
		new SequenceImplCustom
	}
		
	/**
	 * @return a custom form of SubsequenceImpl.
	 */
	override createSubsequence() {
		new SubsequenceImplCustom
	}
		
	/**
	 * @return a custom form of BinaryMessageSetImpl.
	 */
	override createBinaryMessageSet() {
		new BinaryMessageSetImplCustom
	}
	
	/**
	 * @return a custom form of ExtensionalMessageSetImpl.
	 */
	override createExtensionalMessageSet() {
		new ExtensionalMessageSetImplCustom
	}
	
	/**
	 * @return a custom form of UniverseMessageSetImpl.
	 */
	override createUniverseMessageSet() {
		new UniverseMessageSetImplCustom
	}
	
	/**
	 * @return a custom form of RefMessageSetImpl.
	 */
	override createRefMessageSet() {
		new RefMessageSetImplCustom
	}
	
	/**
	 * @return a custom form of MessageSpecImpl.
	 */
	override createMessageSpec() {
		new MessageSpecImplCustom
	}
	
	/**
	 * @return a custom form of InstantiationImpl.
	 */
	override createInstantiation() {
		new InstantiationImplCustom
	}
	
	/**
	 * @return a custom form of ConstAssignmentImpl.
	 */
	override createConstAssignment() {
		new ConstAssignmentImplCustom
	}	
}