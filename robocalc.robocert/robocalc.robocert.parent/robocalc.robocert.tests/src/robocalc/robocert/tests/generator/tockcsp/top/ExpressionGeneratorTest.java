package robocalc.robocert.tests.generator.tockcsp.top;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.xtext.testing.InjectWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.top.ExpressionGenerator;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.CspNormaliser;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class ExpressionGeneratorTest {
	@Inject
	private ExpressionFactory ef;
	@Inject
	private ExpressionGenerator eg;
	@Inject
	private CspNormaliser n;

	/**
	 * Tests that generating the Boolean literals works properly.
	 */
	@Test
	void testGenerateBoolExprs() {
		assertGenerates("true", ef.bool(true));
		assertGenerates("false", ef.bool(false));
	}

	/**
	 * Tests that generating integer literals works properly.
	 */
	@Test
	void testGenerateIntExprs() {
		assertGenerates("0", ef.integer(0));
		assertGenerates("-1", ef.integer(-1));
		assertGenerates("42", ef.integer(42));
	}

	/**
	 * Asserts that the given input generates CSP-M that tidies to the expected
	 * output.
	 * 
	 * @param expected the expected output.
	 * @param input    the input expression.
	 */
	private void assertGenerates(String expected, CertExpr input) {
		assertEquals(expected, n.tidy(eg.generate(input)));
	}
}