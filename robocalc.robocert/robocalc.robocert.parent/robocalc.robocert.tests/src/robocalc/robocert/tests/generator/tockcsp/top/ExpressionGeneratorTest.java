package robocalc.robocert.tests.generator.tockcsp.top;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.xtext.testing.InjectWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.top.ExpressionGenerator;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.LogicalOperator;
import robocalc.robocert.model.robocert.RelationOperator;
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
		assertGenerates("42", ef.integer(42));
		// This last case is unlikely to happen in practice, because the parser
		// would consider -1 to be -(1).
		assertGenerates("-1", ef.integer(-1));
	}
	
	/**
	 * Tests that generating minus expressions works properly.
	 */
	@Test
	void testGenerateMinusExprs() {
		assertGenerates("-(1)", ef.minus(ef.integer(1)));
		assertGenerates("-(-(42))", ef.minus(ef.minus(ef.integer(42))));
		assertGenerates("-(const_x)", ef.minus(ef.constant("x")));
	}
	
	/**
	 * Tests that generating logical expressions works properly.
	 */
	@Test
	void testGenerateLogicalExprs() {
		assertGenerates("(true) and (false)", ef.logic(LogicalOperator.AND, ef.bool(true), ef.bool(false)));
		assertGenerates("(Bnd__x) or (const_y)", ef.logic(LogicalOperator.OR, ef.binding("x"), ef.constant("y")));
	}

	/**
	 * Tests that generating relational expressions works properly.
	 */
	@Test
	void testGenerateRelationExprs() {
		assertGenerates("(42) <= (56)", ef.rel(RelationOperator.LE, ef.integer(42), ef.integer(56)));
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
