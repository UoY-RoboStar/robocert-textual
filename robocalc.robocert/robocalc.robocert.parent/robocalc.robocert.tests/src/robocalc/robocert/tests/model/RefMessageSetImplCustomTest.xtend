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
 * Tests any custom functionality on RefMessageSets, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class RefMessageSetImplCustomTest {
	@Inject RobocertFactory rf

	/**
	 * Tests to make sure isActive is false on null references.
	 */
	@Test
	def testIsActive_NullRef() {
		rf.createRefMessageSet.active.assertFalse
	}
	
	/**
	 * Tests to make sure isActive is false on references to named
	 * sets with null contents.
	 */
	@Test
	def testIsActive_NullIndirect() {
		val mset = rf.createRefMessageSet=>[
			set = rf.createNamedMessageSet
		]
		mset.active.assertFalse
	}
	
	/**
	 * Tests to make sure isActive is true on references to named
	 * sets with universe contents.
	 */
	@Test
	def testIsActive_Universe() {
		val mset = rf.createRefMessageSet=>[
			set = rf.createNamedMessageSet=>[
				set = rf.createUniverseMessageSet
			]
		]
		mset.active.assertTrue
	}
}