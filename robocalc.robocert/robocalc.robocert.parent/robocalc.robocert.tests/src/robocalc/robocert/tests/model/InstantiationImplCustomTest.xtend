package robocalc.robocert.tests.model

import circus.robocalc.robochart.RoboChartFactory
import com.google.inject.Inject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.model.robocert.RoboCertFactory
import robocalc.robocert.tests.RoboCertInjectorProvider

import static extension org.junit.jupiter.api.Assertions.*
import robocalc.robocert.model.robocert.IntExpr
import robocalc.robocert.model.robocert.CertExpr

/**
 * Tests any custom functionality on Instantiations, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider)
class InstantiationImplCustomTest {
	@Inject RoboCertFactory rf
	@Inject RoboChartFactory cf

	@Test
	def testGetConstant() {
		val x1 = cf.createVariable=>[
			name = "foo"
		]
		val x2 = EcoreUtil.copy(x1)
		val y1 = cf.createVariable=>[
			name = "bar"
		]
		val y2 = EcoreUtil.copy(y1)
		
		val inst = rf.createInstantiation=>[
			assignments.add(
				rf.createConstAssignment=>[
					constants.add(x1)
					constants.add(y2)
					value = rf.createIntExpr=>[value = 42]
				]
			)
			assignments.add(
				rf.createConstAssignment=>[
					constants.add(x2)
					value = rf.createIntExpr=>[value = 24]
				]
			)
		]
		
		inst.getConstant(x1).assertIntWithValue(42)
		inst.getConstant(x2).assertIntWithValue(24)
		inst.getConstant(y1).assertNull
		inst.getConstant(y2).assertIntWithValue(42)
	}
	
	private def void assertIntWithValue(CertExpr it, int expected) {
		assertNotNull
		expected.assertEquals((it as IntExpr).value)
	}
}