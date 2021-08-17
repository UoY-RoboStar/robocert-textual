package robocalc.robocert.tests.model

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.model.robocert.RefMessageSet
import robocalc.robocert.model.robocert.RobocertFactory
import robocalc.robocert.tests.RoboCertInjectorProvider

import static extension org.junit.Assert.assertFalse
import static extension org.junit.Assert.assertTrue
import robocalc.robocert.model.robocert.MessageSet
import robocalc.robocert.model.robocert.util.SetFactory

/**
 * Tests any custom functionality on RefMessageSets, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class RefMessageSetImplCustomTest {
	@Inject RobocertFactory rf
	@Inject extension SetFactory

	/**
	 * Tests isActive on various forms of reference.
	 */
	@Test
	def testIsActive() {
		nullRef.active.assertFalse
		nullIndirectRef.active.assertFalse
		refTo(empty).active.assertFalse
		
		refTo(single).active.assertTrue
		refTo(universe).active.assertTrue
	}

	/**
	 * Tests isUniversal on various forms of reference.
	 */
	@Test
	def testIsUniversal() {
		nullRef.universal.assertFalse
		nullIndirectRef.universal.assertFalse
		refTo(empty).universal.assertFalse
		refTo(single).universal.assertFalse
		
		refTo(universe).universal.assertTrue
	}
	
	private def nullRef() {
		rf.createRefMessageSet
	}
	
	private def nullIndirectRef() {
		nullRef=>[
			set = rf.createNamedMessageSet
		]
	}
	
	private def RefMessageSet refTo(MessageSet ms) {
		nullIndirectRef=>[
			set.set = ms
		]
	}
	
	private def single() {
		rf.createExtensionalMessageSet=>[
			messages.add(rf.createMessageSpec)
		]
	}
}