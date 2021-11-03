package robocalc.robocert.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.LogicalOperator;
import robocalc.robocert.model.robocert.RelationOperator;
import robocalc.robocert.model.robocert.util.ExpressionFactory;

/**
 * Tests the parsing of RoboCert expressions.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class CertExprParsingTest {
	@Inject
	private ParseTestHelper pt;
	@Inject
	private ExpressionFactory ef;

	/**
	 * Tests whether parsing Boolean literals works.
	 */
	@Test
	void testParseBoolean() {
		assertParse(ef.bool(true), "true");
		assertParse(ef.bool(false), "false");
	}

	/**
	 * Tests whether parsing integer literals works.
	 */
	@Test
	void testParseInteger() {
		assertParse(ef.integer(0), "0");
		assertParse(ef.integer(42), "42");
	}

	/**
	 * Tests whether parsing logical expressions works.
	 */
	@Test
	void testParseLogical() {
		assertParse(ef.logic(LogicalOperator.AND, ef.bool(true), ef.bool(false)), "true /\\ false");
		// TODO(@MattWindsor91): work out how to get xtext to be able to resolve
		// some constant and binding names. We can't test against them until it
		// can.

		// assertParse(ef.rel(RelationOperator.LE, ef.binding("x"), ef.constant("K")),
		// "@x <= K");

		// TODO(@MattWindsor91): composite logical expressions
	}
	
	/**
	 * Tests whether parsing relational expressions works.
	 */
	@Test
	void testParseRelation() {
		assertParse(ef.rel(RelationOperator.NE, ef.integer(0), ef.integer(42)), "0 != 42");
		// TODO(@MattWindsor91): work out how to get xtext to be able to resolve
		// some constant and binding names. We can't test against them until it
		// can.

		// assertParse(ef.rel(RelationOperator.LE, ef.binding("x"), ef.constant("K")),
		// "@x <= K");

		// TODO(@MattWindsor91): composite relations
	}

	/**
	 * Tests whether parsing negated expressions works.
	 */
	@Test
	void testParseMinus() {
		assertParse(ef.minus(ef.integer(1)), "-1");
	}

	private void assertParse(CertExpr expected, String input) {
		var result = pt.parse(pt.liftExpr(input));
		// can't use normal equality here
		assertTrue(EcoreUtil2.equals(expected, pt.unliftExpr(result)));
	}

}
