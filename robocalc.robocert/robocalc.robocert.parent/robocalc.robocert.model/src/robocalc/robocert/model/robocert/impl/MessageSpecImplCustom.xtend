package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.MessageSpecImpl
import robocalc.robocert.model.robocert.MessageDirection
import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.model.robocert.SequenceGroup

/**
 * Inserts actor-seeking functionality into MessageSpecImpl.
 */
class MessageSpecImplCustom extends MessageSpecImpl {
	/**
	 * Gets the target of the gap message spec by walking back to the sequence
	 * group.
	 * 
	 * @return the target, if we are able to get it.
	 */
	override basicGetTarget() {
		group?.target
	}
	
	/**
	 * Gets the 'from' of the gap message spec by walking back to the sequence
	 * group.
	 * 
	 * @return the source actor, if we are able to get it.
	 */
	override basicGetFrom() {
		getEnd(direction == MessageDirection::OUTBOUND)
	}

	/**
	 * Gets the 'to' of the gap message spec by walking back to the sequence
	 * group.
	 * 
	 * @return the destination actor, if we are able to get it.
	 */
	override basicGetTo() {
		getEnd(direction == MessageDirection::INBOUND)
	}
	
	/**
	 * Gets the sequence group of the gap message spec.
	 * 
	 * @return the sequence group, if we are able to get it.
	 */
	def private getGroup() {
		// TODO(@MattWindsor91): safer way to do this?
		EcoreUtil2.getContainerOfType(this, SequenceGroup)
	}
	
	def private getEnd(boolean isTarget) {
		isTarget ? group?.target : group?.world
	}
}