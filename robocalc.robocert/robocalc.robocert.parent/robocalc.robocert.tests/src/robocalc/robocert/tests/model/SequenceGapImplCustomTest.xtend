package robocalc.robocert.tests.model

import com.google.inject.Inject
import robocalc.robocert.model.robocert.RobocertFactory
import static extension org.junit.Assert.assertTrue
import static extension org.junit.Assert.assertFalse
import org.junit.jupiter.api.Test
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.RoboCertInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.junit.jupiter.api.^extension.ExtendWith

/**
 * Tests any custom functionality on SequenceGaps, and also tests that the
 * factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class SequenceGapImplCustomTest {
	@Inject RobocertFactory rf
	
	/**
	 * Tests that 'active' property handles a universal allow set
	 * properly.
	 */
	@Test
	def testIsActive_UniversalAllow() {
		val gap = rf.createSequenceGap => [
			allowed = rf.createUniverseGapMessageSet
			forbidden = rf.createExtensionalGapMessageSet
		]
		
		gap.active.assertTrue
	}
	
	/**
	 * Tests that 'active' property handles a non-empty extensional
	 * allow set properly.
	 */
	@Test
	def testIsActive_NonEmptyExtensionalAllow() {
		val gap = rf.createSequenceGap => [
			allowed = rf.createExtensionalGapMessageSet => [
				messages.add(rf.createGapMessageSpec)
			]
			forbidden = rf.createExtensionalGapMessageSet
		]
		
		gap.active.assertTrue
	}

	
	/**
	 * Tests that 'active' property handles a empty extensional
	 * allow set properly.
	 */
	@Test
	def testIsActive_EmptyExtensionalAllow() {
		val gap = rf.createSequenceGap => [
			allowed = rf.createExtensionalGapMessageSet
			forbidden = rf.createExtensionalGapMessageSet
		]
		
		gap.active.assertFalse
	}
}