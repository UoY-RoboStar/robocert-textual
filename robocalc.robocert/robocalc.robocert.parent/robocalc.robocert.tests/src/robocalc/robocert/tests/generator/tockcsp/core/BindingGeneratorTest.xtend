package robocalc.robocert.tests.generator.tockcsp.core

import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import org.junit.jupiter.api.Test
import com.google.inject.Inject
import robocalc.robocert.tests.util.CspNormaliser
import robocalc.robocert.model.robocert.RoboCertFactory
import static org.junit.Assert.assertEquals
import robocalc.robocert.generator.tockcsp.core.BindingGenerator

/**
 * Tests the binding CSP generator.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider)
class BindingGeneratorTest {
	@Inject RoboCertFactory rf
	@Inject extension BindingGenerator
	@Inject extension CspNormaliser
	
	//
	// Input
	//
	
	/**
	 * Tests that generating an input name for an empty binding translates into
	 * a '_' in CSP.
	 */
	@Test
	def void generateInputWildcard() {
		assertBindingGeneratesInput(null, "_")
	}
	
	/**
	 * Tests that generating an input name for a named binding translates into
	 * a deterministic mangled form in CSP.
	 */
	@Test
	def void generateInputBound() {
		assertBindingGeneratesInput("Foo", "Bnd__Foo")
	}
	
	//
	// Argument
	//

	/**
	 * Tests that generating an argument name for an empty binding fills in the
	 * index then uses the usual deterministic mangled form
	 */
	@Test
	def void generateArgumentWildcard() {
		assertBindingGeneratesArgument(null, "Bnd__0")
	}
	
	/**
	 * Tests that generating an input name for a named binding translates into
	 * a deterministic mangled form in CSP.
	 */
	@Test
	def void generateArgumentBound() {
		assertBindingGeneratesArgument("Foo", "Bnd__Foo")
	}

	//
	// Test helpers
	//

	private def void assertBindingGeneratesArgument(String name, CharSequence expected) {
		assertEquals(expected, name.binding.generateArgumentName(0).tidy)
	}
	
	private def void assertBindingGeneratesInput(String name, CharSequence expected) {
		assertEquals(expected, name.binding.generateInputName.tidy)
	}
	
	private def binding(String n) {
		rf.createBinding => [name = n]
	}
}