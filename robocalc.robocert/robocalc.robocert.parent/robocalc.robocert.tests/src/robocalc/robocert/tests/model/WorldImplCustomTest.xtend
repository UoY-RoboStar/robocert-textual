package robocalc.robocert.tests.model


import com.google.inject.Inject
import robocalc.robocert.model.robocert.RoboCertFactory
import static extension org.junit.Assert.*
import org.junit.jupiter.api.Test
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.RoboCertInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.^extension.ExtendWith

/**
 * Tests any custom functionality on Worlds, and also tests that the
 * factory resolves them correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class WorldImplCustomTest {
	@Inject RoboCertFactory rf
	
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
	 * Tests that the string representation is correct.
	 */
	@Test
	def testToString() {
		"world".assertEquals(example.toString)
	}
	
	private def example() {
		rf.createWorld=>[
			group = rf.createSequenceGroup
		]
	}
}