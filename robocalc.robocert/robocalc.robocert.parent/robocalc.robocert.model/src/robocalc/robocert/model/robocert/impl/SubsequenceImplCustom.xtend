package robocalc.robocert.model.robocert.impl

import org.eclipse.emf.ecore.EObject
import robocalc.robocert.model.robocert.Sequence

/**
 * Adds derived operation definitions to SubsequenceImpl.
 */
class SubsequenceImplCustom extends SubsequenceImpl {
	/**
	 * @return the parent sequence of this subsequence.
	 */
	override basicGetSequence() {
		for (var EObject ec = this.eContainer; ec !== null; ec = ec.eContainer) {
			switch ec {
				Sequence: return ec
			}
		}
	}
}