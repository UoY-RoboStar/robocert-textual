package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.SequenceGroupImpl
import org.eclipse.emf.common.util.BasicEList

/**
 * Adds derived operation definitions to SequenceGroupImpl.
 */
class SequenceGroupImplCustom extends SequenceGroupImpl {
	/**
	 * Re-points actors to resolve to the sequence's target and world
	 * (in that order).
	 * 
	 * @return the target's actors.
	 */	
	override getActors() {
		new BasicEList => [
			add(target)
			add(world)
		]
	}
}