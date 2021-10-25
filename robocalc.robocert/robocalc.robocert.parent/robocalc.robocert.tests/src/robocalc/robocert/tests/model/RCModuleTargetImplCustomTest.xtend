package robocalc.robocert.tests.model


import com.google.inject.Inject
import robocalc.robocert.model.robocert.RoboCertFactory
import static extension org.junit.Assert.*
import org.junit.jupiter.api.Test
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.RoboCertInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.^extension.ExtendWith
import circus.robocalc.robochart.RoboChartFactory

/**
 * Tests any custom functionality on RCModuleTargets, and also tests that the
 * factory resolves them correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class RCModuleTargetImplCustomTest {
	@Inject RoboCertFactory rf
	@Inject RoboChartFactory cf
	
	/**
	 * Tests that anyGroup and group give the same, non-null result.
	 */
	@Test
	def testAnyGroup() {
		val x = example
		x?.anyGroup.assertNotNull
		x.group.assertEquals(x.anyGroup)
	}
	
	/**
	 * Tests that element and module give the same, non-null result.
	 */
	@Test
	def testElement() {
		val x = example
		x?.element.assertNotNull
		x.module.assertEquals(x.element)
	}
	
	/**
	 * Tests that the string representation is correct.
	 */
	@Test
	def testToString() {
		"module foo".assertEquals(example.toString)
	}
	
	private def example() {
		rf.createRCModuleTarget=>[
			module = cf.createRCModule=>[
				name = "foo"
			]
			group = rf.createSequenceGroup
		]
	}
}