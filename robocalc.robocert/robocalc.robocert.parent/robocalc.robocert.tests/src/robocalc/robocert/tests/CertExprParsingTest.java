package robocalc.robocert.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.StringJoiner;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.CertPackage;
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
	private ParseHelper<CertPackage> parseHelper;
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
	 * Tests whether parsing integer literals works.
	 */
	@Test
	void testParseRelation() {
		assertParse(ef.rel(RelationOperator.NE, ef.integer(0), ef.integer(42)), "0 != 42");
		assertParse(ef.rel(RelationOperator.LE, ef.binding("x"), ef.constant("K")), "@x <= K");
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
		var result = assertDoesNotThrow(() -> parseHelper.parse(lift(input)));
		assertNotNull(result);
		var errors = result.eResource().getErrors();
		if (!errors.isEmpty()) {
			var sb = new StringJoiner(", ", "Unexpected errors: ", "");
			errors.forEach((x) -> sb.add(x.toString()));
			fail(sb.toString());
		}
	}
	
	private String lift(String input) {
		return """
			sequence group X (module W -> world):
				sequence Y:
					<-event Z(""" + input + ")";
	}
}
