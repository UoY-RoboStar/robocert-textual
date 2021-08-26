package robocalc.robocert.model.robocert.impl

import circus.robocalc.robochart.Variable

/**
 * Custom instantiation implementation, adding constant access.
 */
class InstantiationImplCustom extends InstantiationImpl {
	override getConstant(Variable v) {
		assignments.findFirst[hasConstant(v)]?.value
	}
}