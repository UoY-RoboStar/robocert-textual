package robocalc.robocert.model.robocert.impl

import robocalc.robocert.model.robocert.impl.ConstAssignmentImpl
import circus.robocalc.robochart.Variable

class ConstAssignmentImplCustom extends ConstAssignmentImpl {
	override hasConstant(Variable v) {
		// The normal RoboChart equality test compares by name, which doesn't
		// account for the variables being defined in different contexts.
		// So we can't use constants.contains here.
		constants.exists[constantsEqual(v)]
	}
	
	private def constantsEqual(Variable x, Variable y) {
		x === y
	}
}