package robocalc.robocert.tests.generator.csp

import circus.robocalc.robochart.Event
import circus.robocalc.robochart.PrimitiveType
import circus.robocalc.robochart.RoboChartFactory
import circus.robocalc.robochart.TypeRef
import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.generator.csp.MessageSpecGenerator
import robocalc.robocert.model.robocert.MessageTopic
import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.tests.RoboCertInjectorProvider
import robocalc.robocert.model.robocert.ArrowMessageSpec
import static extension org.junit.Assert.assertEquals
import robocalc.robocert.model.robocert.World
import robocalc.robocert.model.robocert.Target
import circus.robocalc.robochart.RCModule
import robocalc.robocert.model.robocert.Argument

/**
 * Tests the message spec CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class MessageSpecGeneratorTest {
	@Inject RobocertFactory rcert
	@Inject RoboChartFactory rc
	@Inject extension MessageSpecGenerator

	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	def void generatePrefixIntEventArrowWithRest() {
		"test::event.in?_".assertEquals(intEvent.topic.arrowSpec(restArg).generatePrefix.tidy)
	}
	
	/**
	 * Tests prefix generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	def void generatePrefixIntEventArrowWithInt() {
		"test::event.in.42".assertEquals(intEvent.topic.arrowSpec(intArg(42)).generatePrefix.tidy)
	}
	
	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing a rest ('...') argument only.
	 */
	@Test
	def void generateCSPEventSetIntEventArrowWithRest() {
		// note the lack of ?_
		"test::event.in".assertEquals(intEvent.topic.arrowSpec(restArg).generateCSPEventSet.tidy)
	}
	
	/**
	 * Tests event set generation of an arrow message set concerning an integer event
	 * against an argument list containing an integer argument only.
	 */
	@Test
	def void generateCSPEventSetIntEventArrowWithInt() {
		"test::event.in.56".assertEquals(intEvent.topic.arrowSpec(intArg(56)).generateCSPEventSet.tidy)
	}
		
	def private String tidy(CharSequence it) {
		toString.strip.replaceAll("  *", " ")
	}
	
	def private ArrowMessageSpec arrowSpec(MessageTopic t, Argument... args) {
		rcert.createArrowMessageSpec => [
			topic = t
			to = target
			from = world
			arguments.addAll(args)
		]
	}

	def private Argument intArg(int v) {
		rcert.createExpressionArgument => [
			expr = rc.createIntegerExp => [
				value = v
			]
		]
	}
	
	def private Argument restArg() {
		rcert.createRestArgument
	}
	
	def private MessageTopic topic(Event e) {
		rcert.createEventTopic => [ event = e ]
	}
	
	def private Event intEvent() {
		rc.createEvent => [
			name = "event"
			type = intTypeRef
		]
	}
	
	private def TypeRef intTypeRef() {
		rc.createTypeRef => [ ref = intType ]
	}
	
	private def PrimitiveType intType() {
		rc.createPrimitiveType => [ name = "int" ]
	}
	
	private def Target target() {
		rcert.createRCModuleTarget => [ module = rcModule ]
	}
	
	private def World world() {
		rcert.createWorld
	}
	
	private def RCModule rcModule() {
		rc.createRCModule => [ name = "test" ]
	}
}