package robocalc.robocert.tests.generator.tockcsp.seq


import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.generator.tockcsp.seq.MessageSetGenerator
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import static extension org.junit.Assert.assertEquals
import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.tests.util.CspNormaliser
import robocalc.robocert.tests.util.MessageSpecFactory
import robocalc.robocert.model.robocert.MessageDirection

/**
 * Tests the message set CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class MessageSetGeneratorTest {
	@Inject extension MessageSetGenerator
	@Inject extension CspNormaliser
	@Inject RobocertFactory rf
	@Inject extension MessageSpecFactory
	
	/**
	 * Tests set generation of an empty extensional message set.
	 */
	@Test
	def void generateEmptyExtensional() {
		"{}".assertEquals(rf.createExtensionalMessageSet.generate.tidy)
	}
	
	/**
	 * Tests set generation of an simple single-occupant extensional message set.
	 */
	@Test
	def void generateSimpleSingletonExtensional() {
		val spec = intEvent.topic.spec(MessageDirection::OUTBOUND, intArg(42))
		val set = gapParent => [
			messages.add(spec)
		]
		"{| test::event.out.42 |}".assertEquals(set.generate.tidy)
	}
}
