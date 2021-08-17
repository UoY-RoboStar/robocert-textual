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
import robocalc.robocert.model.robocert.util.SetFactory

/**
 * Tests any custom functionality on ExtensionalMessageSets, and also tests
 * that the factory resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class BinaryMessageSetImplCustomTest {
	@Inject RobocertFactory rf
	@Inject extension SetFactory sf

	/**
	 * Tests to make sure isActive is handled correctly on unions.
	 */
	@Test
	def testIsActive_Union() {
		union(empty, empty).active.assertFalse
		
		union(universe, empty).active.assertTrue
		union(empty, universe).active.assertTrue
		union(universe, universe).active.assertTrue
		
		union(single, empty).active.assertTrue
		union(empty, single).active.assertTrue
		union(single, single).active.assertTrue
		
		union(single, universe).active.assertTrue
		union(universe, single).active.assertTrue			
	}
	
	/**
	 * Tests to make sure isActive is handled correctly on intersections.
	 */
	@Test
	def testIsActive_Intersection() {
		inter(empty, empty).active.assertFalse
		
		inter(universe, empty).active.assertFalse
		inter(empty, universe).active.assertFalse
		inter(universe, universe).active.assertTrue
		
		inter(single, empty).active.assertFalse
		inter(empty, single).active.assertFalse
		
		// isActive isn't required to check elements, so we don't specify inter(single, single)
		
		inter(single, universe).active.assertTrue
		inter(universe, single).active.assertTrue		
	}

	/**
	 * Tests to make sure isActive is handled correctly on differences.
	 */
	@Test
	def testIsActive_Difference() {
		diff(empty, empty).active.assertFalse
		
		diff(universe, empty).active.assertTrue
		diff(empty, universe).active.assertFalse
		// we expect diff to handle the special case of \universe
		diff(universe, universe).active.assertFalse
		
		diff(single, empty).active.assertTrue
		diff(empty, single).active.assertFalse
		
		// isActive isn't required to check elements, so we don't specify diff(single, single)

		// see above
		diff(single, universe).active.assertFalse
		diff(universe, single).active.assertTrue	
	}

	/**
	 * Tests to make sure isUniversal is handled correctly on unions.
	 */
	@Test
	def testIsUniversal_Union() {	
		// any union including the universe is universal
		union(universe, empty).universal.assertTrue
		union(empty, universe).universal.assertTrue
		union(universe, universe).universal.assertTrue
		union(single, universe).universal.assertTrue
		union(universe, single).universal.assertTrue

		// anything else is not
		union(empty, empty).universal.assertFalse
		union(single, empty).universal.assertFalse
		union(empty, single).universal.assertFalse
		union(single, single).universal.assertFalse
	}
	
	/**
	 * Tests to make sure isUniversal is handled correctly on intersections.
	 */
	@Test
	def testIsUniversal_Intersection() {
		// only universes intersected with universes remain universal
		inter(universe, universe).universal.assertTrue
			
		// everything else does not
		inter(empty, empty).universal.assertFalse		
		inter(empty, single).universal.assertFalse
		inter(empty, universe).universal.assertFalse
		inter(single, empty).universal.assertFalse
		inter(single, single).universal.assertFalse
		inter(single, universe).universal.assertFalse
		inter(universe, empty).universal.assertFalse
		inter(universe, single).universal.assertFalse
	}

	/**
	 * Tests to make sure isActive is handled correctly on differences.
	 */
	@Test
	def testIsUniversal_Difference() {
		// only universes with nothing removed remain universal
		diff(universe, empty).universal.assertTrue
		
		// everything else does not
		diff(empty, empty).universal.assertFalse
		diff(empty, universe).universal.assertFalse
		diff(empty, single).universal.assertFalse
		diff(single, empty).universal.assertFalse
		diff(single, universe).universal.assertFalse
		diff(single, single).universal.assertFalse
		diff(universe, single).universal.assertFalse
		diff(universe, universe).universal.assertFalse
	}
	
	private def single() {
		rf.createExtensionalMessageSet=>[
			messages.add(rf.createMessageSpec)
		]
	}
}