package robocalc.robocert.tests.model

import circus.robocalc.robochart.RoboChartFactory
import circus.robocalc.robochart.Variable
import circus.robocalc.robochart.VariableModifier
import com.google.inject.Inject
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.model.robocert.ConstAssignment
import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.tests.RoboCertInjectorProvider

import static extension org.junit.jupiter.api.Assertions.*

/**
 * Tests any custom functionality on ConstAssignments, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class ConstAssignmentImplCustomTest {
	@Inject RobocertFactory rf
	@Inject RoboChartFactory cf

	@Test
	def testHasConstant_Basic() {
		val x = cf.createVariable=>[
			name = "foo"
		]
		val y = cf.createVariable=>[
			name = "bar"
		]
		
		val ca = assignment(x)
		ca.hasConstant(x).assertTrue
		ca.hasConstant(y).assertFalse		
	}
	
	@Test
	def testHasConstant_SameNameDifferentParents() {
		val x = cf.createVariable=>[
			name = "foo"
		]
		val y = EcoreUtil2.copy(x)
		
		cf.createControllerDef=>[
			name = "X"
			variableList.add(cf.createVariableList=>[
				vars.add(x)
				modifier = VariableModifier::CONST
			])
		]
		cf.createControllerDef=>[
			name = "Y"
			variableList.add(cf.createVariableList=>[
				vars.add(y)
				modifier = VariableModifier::CONST				
			])
		]
		
		val ca = assignment(x)
		ca.hasConstant(x).assertTrue
		ca.hasConstant(y).assertFalse
		
		ca.constants.add(y)
		ca.hasConstant(x).assertTrue
		ca.hasConstant(y).assertTrue
	}
	
	private def ConstAssignment assignment(Variable x) {
		rf.createConstAssignment=>[
			constants.add(x)
			value = rf.createRAIntLit=>[value = 4]
		]
	}
}