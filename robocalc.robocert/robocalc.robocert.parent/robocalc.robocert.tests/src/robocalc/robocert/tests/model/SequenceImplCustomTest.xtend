package robocalc.robocert.tests.model

import com.google.inject.Inject
import robocalc.robocert.model.robocert.RobocertFactory
import static extension org.junit.Assert.assertArrayEquals
import static extension org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.RoboCertInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.^extension.ExtendWith

/**
 * Tests any custom functionality on Sequences, and also tests that the factory
 * resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class SequenceImplCustomTest {
	@Inject RobocertFactory rf
	
	/**
	 * Tests that the 'actors' derived property pulls the target and world
	 * correctly.
	 */
	@Test
	def testActors() {
		val seq = rf.createSequence
		val sgroup = rf.createSequenceGroup => [
			target = rf.createRCModuleTarget
			world = rf.createWorld
			sequences.add(seq)
		]

		sgroup.target.assertEquals(seq.target)
		sgroup.world.assertEquals(seq.world)
		#[seq.target, seq.world].assertArrayEquals(seq.actors.toArray)
	}
}