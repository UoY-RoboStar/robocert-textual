package robocalc.robocert.tests.generator.csp


import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.generator.csp.MessageSetGenerator
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import static extension org.junit.Assert.assertEquals
import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.tests.util.CspNormaliser
import java.util.Collections

/**
 * Tests the message set CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class MessageSetGeneratorTest {
	@Inject extension MessageSetGenerator
	@Inject extension CspNormaliser
	@Inject RobocertFactory rf
	
	/**
	 * Tests set generation of an empty extensional message set.
	 */
	@Test
	def void generateEmptyExtensional() {
		"{||}".assertEquals(rf.createExtensionalMessageSet.generate(Collections.EMPTY_LIST).tidy)
	}
}