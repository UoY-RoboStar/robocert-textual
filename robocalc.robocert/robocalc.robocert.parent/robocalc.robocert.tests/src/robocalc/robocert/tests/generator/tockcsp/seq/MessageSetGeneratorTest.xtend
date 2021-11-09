package robocalc.robocert.tests.generator.tockcsp.seq


import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.generator.tockcsp.seq.MessageSetGenerator
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import static extension org.junit.Assert.assertEquals
import robocalc.robocert.tests.util.CspNormaliser
import robocalc.robocert.tests.util.MessageSpecFactory
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.model.robocert.MessageSet
import robocalc.robocert.model.robocert.util.SetFactory
import robocalc.robocert.model.robocert.util.MessageFactory

/**
 * Tests the message set CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class MessageSetGeneratorTest {
	@Inject extension MessageSetGenerator
	@Inject extension CspNormaliser
	@Inject extension MessageFactory
	@Inject extension MessageSpecFactory
	@Inject SetFactory sf
	
	/**
	 * Tests set generation of an empty extensional message set.
	 */
	@Test
	def void generateEmptyExtensional() {
		sf.empty.assertGenerates("{}")
	}
	
	/**
	 * Tests set generation of an simple single-occupant extensional message set.
	 */
	@Test
	def void generateSimpleSingletonExtensional() {
		val spec = intEvent.eventTopic.spec(MessageDirection::OUTBOUND.directional, intArg(42))
		sf.singleton(spec).assertGenerates("{| test::event.out.42 |}")
	}
	
	private def assertGenerates(MessageSet it, CharSequence expected) {
		// This is needed to make sure the namespace of events is correct.
		setupAsGap
		expected.assertEquals(generate.tidy)
	}
}
