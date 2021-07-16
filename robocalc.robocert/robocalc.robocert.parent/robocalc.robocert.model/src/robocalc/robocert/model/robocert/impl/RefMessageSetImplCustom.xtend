package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.RefMessageSetImpl

class RefMessageSetImplCustom extends RefMessageSetImpl {
	/**
	 * Ref gap messages sets are active if their referred-to set is.
	 * 
	 * @return an optimistic estimate of whether there is at least one message in the set.
	 */
	override isActive() {
		val s = set?.set;
		(s === null) ? false : s.active
	}
	
	/**
	 * Ref gap messages sets are universal if their referred-to set is.
	 * 
	 * @return a pessimistic estimate of whether every message is in the set.
	 */
	override isUniversal() {
		val s = set?.set;
		(s === null) ? false : s.universal
	}	
}