package robocalc.robocert.tests.generator.csp

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.generator.csp.MessageSpecGenerator
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import static extension org.junit.Assert.assertEquals
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.tests.util.MessageSpecFactory

/**
 * Tests the message spec CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class MessageSpecGeneratorTest {
	@Inject extension MessageSpecGenerator
	@Inject extension MessageSpecFactory

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	def void generatePrefixIntEventArrowWithRest() {
		"test::event.in?_".assertEquals(intEvent.topic.arrowSpec(MessageDirection::INBOUND, restArg).generatePrefix.tidy)
	}
	
	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	def void generatePrefixIntEventArrowWithInt() {	
		"test::event.out.42".assertEquals(intEvent.topic.arrowSpec(MessageDirection::OUTBOUND, intArg(42)).generatePrefix.tidy)
	}
	
	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	def void generateCSPEventSetIntEventArrowWithRest() {
		// note the lack of ?_
		"test::event.in".assertEquals(intEvent.topic.arrowSpec(MessageDirection::INBOUND, restArg).generateCSPEventSet.tidy)
	}
	
	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	def void generateCSPEventSetIntEventArrowWithInt() {
		"test::event.out.56".assertEquals(intEvent.topic.arrowSpec(MessageDirection::OUTBOUND, intArg(56)).generateCSPEventSet.tidy)
	}
		
	def private String tidy(CharSequence it) {
		toString.strip.replaceAll("  *", " ")
	}

}