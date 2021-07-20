package robocalc.robocert.tests.util

import robocalc.robocert.model.robocert.Actor
import robocalc.robocert.model.robocert.Argument
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.MessageTopic
import robocalc.robocert.model.robocert.RCModuleTarget
import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.model.robocert.World
import circus.robocalc.robochart.RoboChartFactory
import circus.robocalc.robochart.Event
import com.google.inject.Inject
import static extension org.junit.jupiter.api.Assertions.*

/**
 * Provides ways of creating dummy message specifications.
 */
class MessageSpecFactory {
	@Inject RoboChartFactory rc;
	@Inject RobocertFactory rcert;

	/**
	 * Creates an arrow message spec with the given topic, direction, and
	 * arguments, with a fake sequence-group context.
	 * 
	 * @param t     the desired topic.
	 * @param dir   the desired direction.
	 * @param args  the desired arguments.
	 * 
	 * @return a constructed arrow message spec.
	 */
	def MessageSpec arrowSpec(MessageTopic t, MessageDirection dir, Argument... args) {
		spec(t, dir, args) => [s|arrowParent.body = s]
	}

	def private spec(MessageTopic t, MessageDirection dir, Argument... args) {
		rcert.createMessageSpec => [
			topic = t
			direction = dir
			arguments.addAll(args)
		]
	}

	def private arrowParent() {
		rcert.createArrowAction => [
			step = rcert.createActionStep => [
				gap = rcert.createExtensionalMessageSet
				parent = sseq
			]
		]
	}

	/**
	 * Creates a gap message spec with the given topic, direction, and
	 * arguments, with a fake sequence-group context.
	 * 
	 * @param t     the desired topic.
	 * @param dir   the desired direction.
	 * @param args  the desired arguments.
	 * 
	 * @return a constructed gap message spec.
	 */
	def MessageSpec gapSpec(MessageTopic t, MessageDirection dir, Argument... args) {
		spec(t, dir, args) => [s|gapParent.messages.add(s)]
	}

	/**
	 * Sets up a gap message spec parent that has enough context for a
	 * sequence group to be located.
	 */
	def private gapParent() {
		rcert.createExtensionalMessageSet => [ g |
			rcert.createActionStep => [
				gap = g
				parent = sseq
			]
		]
	}

	def Argument intArg(int v) {
		rcert.createExpressionArgument => [
			expr = rc.createIntegerExp => [
				value = v
			]
		]
	}

	def Argument restArg() {
		rcert.createRestArgument
	}

	def MessageTopic topic(Event e) {
		rcert.createEventTopic => [event = e]
	}

	def Event intEvent() {
		rc.createEvent => [
			name = "event"
			type = intTypeRef
		]
	}

	private def intTypeRef() {
		rc.createTypeRef => [ref = intType]
	}

	private def intType() {
		rc.createPrimitiveType => [name = "int"]
	}

	private def sseq() {
		val s = seq
		rcert.createSubsequence => [
			s.body = it
		]
	}

	private def seq() {
		rcert.createSequence => [ x |
			x.group = group
		]
	}

	private def group() {
		rcert.createSequenceGroup => [ x |
			x.target = target
			x.world = world
		]
	}

	private def target() {
		rcert.createRCModuleTarget => [
			module = rcModule
		]
	}

	private def world() {
		rcert.createWorld
	}

	private def rcModule() {
		rc.createRCModule => [name = "test"]
	}

	/**
	 * Checks that it appears to be the arrow factory's mock world.
	 * 
	 * @param it  the actor to check.
	 */
	def expectWorld(Actor it) {
		assertNotNull
		assertTrue(it instanceof World)
	}

	/**
	 * Checks that it appears to be the arrow factory's mock target.
	 * 
	 * @param it  the actor to check.
	 */
	def expectTarget(Actor it) {
		assertNotNull
		switch it {
			RCModuleTarget:
				"test".assertEquals(module.name)
			default:
				fail("not a target")
		}
	}
}
