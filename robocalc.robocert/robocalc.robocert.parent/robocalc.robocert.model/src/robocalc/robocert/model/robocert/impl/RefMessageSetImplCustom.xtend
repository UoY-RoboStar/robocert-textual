package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.RefMessageSetImpl

class RefMessageSetImplCustom extends RefMessageSetImpl {
	/**
	 * Ref gap messages sets are active if their referred-to set is.
	 * 
	 * @return whether there is at least one message in the set.
	 */
	override isActive() {
		val s = set?.set;
		(s === null) ? false : s.active
	}
}