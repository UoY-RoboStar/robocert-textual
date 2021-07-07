package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.ArrowMessageSpecImpl
import robocalc.robocert.model.robocert.MessageDirection

/**
 * Inserts actor-seeking functionality into ArrowMessageSpecImpl.
 */
class ArrowMessageSpecImplCustom extends ArrowMessageSpecImpl {
	/**
	 * Gets the target of the arrow message spec by walking back to the sequence
	 * group.
	 * 
	 * @return the target, if we are able to get it.
	 */
	override basicGetTarget() {
		group?.target
	}
	
	/**
	 * Gets the 'from' of the arrow message spec by walking back to the sequence
	 * group.
	 * 
	 * @return the source actor, if we are able to get it.
	 */
	override basicGetFrom() {
		getEnd(direction == MessageDirection::OUTBOUND)
	}

	/**
	 * Gets the 'to' of the arrow message spec by walking back to the sequence
	 * group.
	 * 
	 * @return the destination actor, if we are able to get it.
	 */
	override basicGetTo() {
		getEnd(direction == MessageDirection::INBOUND)
	}
	
	/**
	 * Gets the sequence group of the arrow message spec.
	 * 
	 * @return the sequence group, if we are able to get it.
	 */
	def private getGroup() {
		parent?.step?.parent?.sequence?.group
	}
	
	def private getEnd(boolean isTarget) {
		isTarget ? group?.target : group?.world
	}
}