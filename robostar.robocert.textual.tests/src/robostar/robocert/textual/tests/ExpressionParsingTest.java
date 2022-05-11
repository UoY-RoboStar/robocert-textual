package robostar.robocert.textual.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import circus.robocalc.robochart.Expression;
import com.google.inject.Inject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.util.ExpressionFactory;

/**
 * Tests the parsing of RoboCert expressions.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class ExpressionParsingTest {
  @Inject private ParseTestHelper pt;
  @Inject private ExpressionFactory ef;

  /** Tests whether parsing Boolean literals works. */
  @Test
  void testParseBoolean() {
    assertParse(ef.bool(true), "true");
    assertParse(ef.bool(false), "false");
  }

  /** Tests whether parsing integer literals works. */
  @Test
  void testParseInteger() {
    assertParse(ef.integer(0), "0");
    assertParse(ef.integer(42), "42");
  }

  /** Tests whether parsing '/\' expressions works. */
  @Test
  void testParseLogical() {
    assertParse(ef.and(ef.bool(true), ef.bool(false)), "true /\\ false");
    // TODO(@MattWindsor91): work out how to get xtext to be able to resolve
    // some constant and binding names. We can't test against them until it
    // can.

    // assertParse(ef.rel(RelationOperator.LE, ef.binding("x"), ef.constant("K")),
    // "@x <= K");

    // TODO(@MattWindsor91): composite logical expressions
  }

  /** Tests whether parsing '!=' expressions works. */
  @Test
  void testParseDifference() {
    assertParse(ef.diff(ef.integer(0), ef.integer(42)), "0 != 42");
    // TODO(@MattWindsor91): work out how to get xtext to be able to resolve
    // some constant and binding names. We can't test against them until it
    // can.

    // assertParse(ef.rel(RelationOperator.LE, ef.binding("x"), ef.constant("K")),
    // "@x <= K");

    // TODO(@MattWindsor91): composite relations
  }

  /** Tests whether parsing negated expressions works. */
  @Test
  void testParseMinus() {
    assertParse(ef.neg(ef.integer(1)), "-1");
  }

  private void assertParse(Expression expected, String input) {
    final var result = pt.parse(pt.liftExpr(input));
    // can't use normal equality here
    assertTrue(EcoreUtil2.equals(expected, pt.unliftExpr(result)));
  }
}
