package robocalc.robocert.model.robocert.impl

/**
 * Implements derived functionality for BinaryMessageSet.
 */
class BinaryMessageSetImplCustom extends BinaryMessageSetImpl {
	/**
	 * @return an optimistic assumption as to whether this message set has any
	 *         messages.
	 */
	override isActive() {
		switch operator {
			case UNION:
				lhs.isActive || rhs.isActive
			case INTERSECTION:
				lhs.isActive && rhs.isActive
			case DIFFERENCE:
				lhs.isActive && !rhs.isUniversal
		}
	}
	
	/**
	 * @return a pessimistic assumption as to whether this message set has every
	 *         message.
	 */
	override isUniversal() {
		switch operator {
			case UNION:
				lhs.isUniversal || rhs.isUniversal
			case INTERSECTION:
				lhs.isUniversal && rhs.isUniversal
			case DIFFERENCE:
				lhs.isUniversal && !rhs.isActive
		}
	}	
}