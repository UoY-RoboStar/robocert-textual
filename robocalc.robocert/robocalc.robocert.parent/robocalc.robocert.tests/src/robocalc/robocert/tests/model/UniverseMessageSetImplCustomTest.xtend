package robocalc.robocert.tests.model

import com.google.inject.Inject
import robocalc.robocert.model.robocert.RobocertFactory
import static extension org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.RoboCertInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.^extension.ExtendWith

/**
 * Tests any custom functionality on UniversalMessageSets, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class UniverseMessageSetImplCustomTest {
	@Inject RobocertFactory rf

	/**
	 * Tests to make sure 'active' is always true on universe gap message
	 * sets.
	 */
	@Test
	def testIsActive() {
		rf.createUniverseMessageSet.active.assertTrue
	}
}