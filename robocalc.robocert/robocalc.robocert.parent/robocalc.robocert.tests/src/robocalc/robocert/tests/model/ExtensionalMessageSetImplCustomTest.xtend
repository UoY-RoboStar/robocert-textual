package robocalc.robocert.tests.model

import com.google.inject.Inject
import robocalc.robocert.model.robocert.RobocertFactory
import static extension org.junit.Assert.assertFalse
import static extension org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.RoboCertInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.^extension.ExtendWith

/**
 * Tests any custom functionality on ExtensionalMessageSets, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class ExtensionalMessageSetImplCustomTest {
	@Inject RobocertFactory rf

	/**
	 * Tests to make sure isActive is false on empty extensional gap message
	 * sets.
	 */
	@Test
	def testIsActive_Empty() {
		rf.createExtensionalMessageSet.active.assertFalse
	}
	
	/**
	 * Tests to make sure isActive is true on nonempty extensional gap message
	 * sets.
	 */
	@Test
	def testIsActive_NonEmpty() {
		val mset = rf.createExtensionalMessageSet=>[
			messages.add(rf.createMessageSpec)
		]
		mset.active.assertTrue
	}

	/**
	 * Tests to make sure isUniversal is false on empty extensional gap message
	 * sets.
	 */
	@Test
	def testIsUniversal_Empty() {
		rf.createExtensionalMessageSet.universal.assertFalse
	}
	
	/**
	 * Tests to make sure isUniversal is false even on nonempty extensional gap message
	 * sets.
	 */
	@Test
	def testIsUniversal_NonEmpty() {
		val mset = rf.createExtensionalMessageSet=>[
			messages.add(rf.createMessageSpec)
		]
		mset.universal.assertFalse
	}	
}