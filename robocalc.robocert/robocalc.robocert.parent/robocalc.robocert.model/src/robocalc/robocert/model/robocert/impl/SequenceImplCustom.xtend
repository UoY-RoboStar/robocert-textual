package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.SequenceImpl
import org.eclipse.emf.common.util.BasicEList

/**
 * Adds derived operation definitions to SequenceImpl.
 */
class SequenceImplCustom extends SequenceImpl {
	/**
	 * Delegates to the sequence's group's target.
	 */	
	override basicGetTarget() {
		group?.target
	}

	/**
	 * Delegates to the sequence's group's world.
	 */	
	override basicGetWorld() {
		group?.world
	}
	
	/**
	 * Delegates to the sequence's group's actors.
	 */	
	override getActors() {
		group?.actors ?: new BasicEList
	}
}