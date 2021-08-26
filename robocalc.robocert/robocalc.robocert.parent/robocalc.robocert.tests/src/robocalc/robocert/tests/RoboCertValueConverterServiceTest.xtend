package robocalc.robocert.tests

import org.eclipse.xtext.conversion.IValueConverter
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.RoboCertValueConverterService

import static extension org.junit.jupiter.api.Assertions.*

/**
 * Tests the value converters for the textual language.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider)
class RoboCertValueConverterServiceTest {
	/**
	 * Tests that lexing then emitting the given CSP fragment works correctly.
	 * 
	 * We test for any issues in the whitespace stripping, for fidelity.
	 */
	@Test
	def TestCspCode_RoundTripLexEmit() {
		val want = "csp-begin\n\tSTOP\ncsp-end"
		val conv = new RoboCertValueConverterService().cspCodeConverter
		roundTripLexEmit(want, conv)
	}

	/**
	 * Tests that emitting then lexing the given CSP fragment works correctly.
	 * 
	 * We test for any issues in the whitespace stripping, for fidelity.
	 */
	@Test
	def TestCspCode_RoundTripEmitLex() {
		val want = "a->\n\tb\n\tc->SKIP"
		val conv = new RoboCertValueConverterService().cspCodeConverter
		roundTripEmitLex(want, conv)
	}
	
	/**
	 * Tests that lexing then emitting the given short literal works correctly.
	 * 
	 * We test for any issues in the whitespace stripping, for fidelity.
	 */
	@Test
	def TestShortCode_RoundTripLexEmit() {
		val want = "<$\n\tSTOP\n$>"
		val conv = new RoboCertValueConverterService().shortCodeConverter
		roundTripLexEmit(want, conv)
	}

	/**
	 * Tests that emitting then lexing the given short literal works correctly.
	 * 
	 * We test for any issues in the whitespace stripping, for fidelity.
	 */
	@Test
	def TestShortCode_RoundTripEmitLex() {
		val want = "a->\n\tb\n\tc->SKIP"
		val conv = new RoboCertValueConverterService().shortCodeConverter
		roundTripEmitLex(want, conv)
	}
	
	private def roundTripLexEmit(String want, IValueConverter<String> conv) {
		want.assertEquals(conv.toString(conv.toValue(want, null)))
	}
	
	private def roundTripEmitLex(String want, IValueConverter<String> conv) {
		want.assertEquals(conv.toValue(conv.toString(want), null))
	}
	
	
}