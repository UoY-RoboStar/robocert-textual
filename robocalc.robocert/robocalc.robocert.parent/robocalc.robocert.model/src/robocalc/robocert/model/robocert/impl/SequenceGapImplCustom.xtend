package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.SequenceGapImpl

/**
 * Adds derived operation definitions to SequenceGapImpl.
 */
class SequenceGapImplCustom extends SequenceGapImpl {
	/**
	 * A sequence gap is active if its allow-set is.
	 * 
	 * This is an overapproximation; it might be that everything in the
	 * allow set is also in the forbid set.
	 * 
	 * @return allowed.isActive.
	 */
	override isActive() {
		allowed.isActive
	}
}