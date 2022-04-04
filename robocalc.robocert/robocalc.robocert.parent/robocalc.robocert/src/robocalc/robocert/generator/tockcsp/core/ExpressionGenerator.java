package robocalc.robocert.generator.tockcsp.core;

import circus.robocalc.robochart.And;
import circus.robocalc.robochart.BinaryExpression;
import circus.robocalc.robochart.BooleanExp;
import circus.robocalc.robochart.Different;
import circus.robocalc.robochart.Div;
import circus.robocalc.robochart.Equals;
import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.FloatExp;
import circus.robocalc.robochart.GreaterOrEqual;
import circus.robocalc.robochart.GreaterThan;
import circus.robocalc.robochart.IntegerExp;
import circus.robocalc.robochart.LessOrEqual;
import circus.robocalc.robochart.LessThan;
import circus.robocalc.robochart.Minus;
import circus.robocalc.robochart.Modulus;
import circus.robocalc.robochart.Mult;
import circus.robocalc.robochart.Neg;
import circus.robocalc.robochart.Or;
import circus.robocalc.robochart.Plus;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator;
import circus.robocalc.robochart.textual.RoboCalcTypeProvider;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import robocalc.robocert.generator.utils.VariableHelper;

/**
 * The RoboCert expression generator.
 * <p>
 * This implements (some of) the RoboChart expression language, but does so in a way with some
 * differences to how the reference expression compiler works.  The main difference is that
 * variables resolve to specification memory slots, and constants to target parameterisation.
 * <p>
 * In the future, RoboCert may target the RoboChart generator directly. It used to in the past, but
 * this proved too complex.
 *
 * @author Matt Windsor
 */
public record ExpressionGenerator(TemporaryVariableGenerator bg, VariableHelper vx,
                                  TypeGenerator typeGen, RoboCalcTypeProvider typeProvider) {

  @Inject
  public ExpressionGenerator {
    Objects.requireNonNull(bg);
    Objects.requireNonNull(vx);
    Objects.requireNonNull(typeGen);
    Objects.requireNonNull(typeProvider);
  }

  /**
   * Generates CSP-M for an expression.
   *
   * @param it the expression to generate.
   * @return CSP-M for the expression.
   */
  public CharSequence generate(Expression it) {
    // TODO(@MattWindsor91): replace this with a type switch when it stops
    // being a preview feature. I tried using it as a preview feature, but
    // couldn't get the UI to load under preview mode.
    if (it instanceof BinaryExpression b) {
      return generateBinary(b);
    }
    if (it instanceof BooleanExp b) {
      return b.getValue();
    }
    if (it instanceof RefExp r && r.getRef() instanceof Variable v) {
      return generateVariable(v);
    }
    if (it instanceof IntegerExp i) {
      return Integer.toString(i.getValue());
    }
    if (it instanceof FloatExp f) {
      throw new IllegalArgumentException(
          // See robochart-csp-gen#39.
          "floating point expressions are unsupported for CSP generation: %s".formatted(f));
    }
    if (it instanceof Neg m) {
      return "-(" + generate(m.getExp()) + ")";
    }
    throw new IllegalArgumentException(
        "unsupported expression (only a few are supported so far): %s".formatted(it));
  }

  private CharSequence generateVariable(Variable v) {
    // in RoboCert, variables are either RoboChart constants or spec-level bindings.
    // This is the main difference between our generator and that of RoboCert.
    return switch (v.getModifier()) {
      case CONST -> vx.constantId(v);
      case VAR -> bg.generateExpressionName(v);
    };
  }

  private CharSequence generateBinary(BinaryExpression it) {
    // TODO(@MattWindsor91): this can likely be optimised for precedence?
    return tryGenerateArithBinary(it).orElseGet(
        () -> "(%s) %s (%s)".formatted(generate(it.getLeft()), generateCspOp(it),
            generate(it.getRight())));
  }

  /**
   * Tries to see if this binary expression is an arithmetic operator; if so, expands it to the
   * checked RoboChart definition.
   *
   * @param it the expression to generate.
   * @return an Optional containing the expression; if the optional is empty, the binary expression
   * is either handled at CSP-level or is ill-formed.
   */
  private Optional<CharSequence> tryGenerateArithBinary(BinaryExpression it) {
    return generateArithOp(it).map(op -> {
      // TODO(@MattWindsor91): type getting from a context
      final var type = typeGen.compileType(typeProvider.typeFor(it));
      return "%s(%s, %s, %s)".formatted(op, generate(it.getLeft()), generate(it.getRight()), type);
    });
  }

  private Optional<String> generateArithOp(BinaryExpression it) {
    if (it instanceof Plus) {
      return Optional.of("Plus");
    }
    if (it instanceof Minus) {
      return Optional.of("Minus");
    }
    if (it instanceof Mult) {
      return Optional.of("Mult");
    }
    if (it instanceof Div) {
      return Optional.of("Div");
    }
    if (it instanceof Modulus) {
      return Optional.of("Modulus");
    }
    return Optional.empty();
  }

  private String generateCspOp(BinaryExpression it) {
    if (it instanceof And) {
      return "and";
    }
    if (it instanceof Or) {
      return "or";
    }
    if (it instanceof LessThan) {
      return "<";
    }
    if (it instanceof LessOrEqual) {
      return "<=";
    }
    if (it instanceof Equals) {
      return "==";
    }
    if (it instanceof Different) {
      return "!=";
    }
    if (it instanceof GreaterOrEqual) {
      return ">=";
    }
    if (it instanceof GreaterThan) {
      return ">";
    }
    throw new IllegalArgumentException(
        "unsupported binary expression (only a few are supported so far): %s".formatted(it));
  }
}