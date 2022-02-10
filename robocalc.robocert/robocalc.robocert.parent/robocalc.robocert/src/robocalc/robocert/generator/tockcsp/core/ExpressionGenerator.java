package robocalc.robocert.generator.tockcsp.core;

import circus.robocalc.robochart.And;
import circus.robocalc.robochart.BinaryExpression;
import circus.robocalc.robochart.BooleanExp;
import circus.robocalc.robochart.Different;
import circus.robocalc.robochart.Equals;
import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.GreaterOrEqual;
import circus.robocalc.robochart.GreaterThan;
import circus.robocalc.robochart.IntegerExp;
import circus.robocalc.robochart.LessOrEqual;
import circus.robocalc.robochart.LessThan;
import circus.robocalc.robochart.Neg;
import circus.robocalc.robochart.Or;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.Variable;
import com.google.inject.Inject;
import robocalc.robocert.generator.utils.VariableExtensions;

/**
 * The RoboCert expression generator.
 *
 * This implements (some of) the RoboChart expression language, but does so in a way with some
 * differences to how the reference expression compiler works.  The main difference is that
 * variables resolve to specification memory slots, and constants to target parameterisation.
 *
 * In the future, RoboCert may target the RoboChart generator directly.
 * It used to in the past, but this proved too complex.
 *
 * @author Matt Windsor
 */
public class ExpressionGenerator {
	@Inject
	private TemporaryVariableGenerator bg;
	@Inject
	private VariableExtensions vx;

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
		if (it instanceof BinaryExpression b)
			return generateBinary(b);
		if (it instanceof BooleanExp b)
			return b.getValue();
		if (it instanceof RefExp r && r.getRef() instanceof Variable v)
			return generateVariable(v);
		if (it instanceof IntegerExp i)
			return Integer.toString(i.getValue());
		if (it instanceof Neg m)
			return "-(" + generate(m.getExp()) + ")";
		throw new IllegalArgumentException("unsupported expression (only a few are supported so far): %s".formatted(it));
	}

	private CharSequence generateVariable(Variable v) {
		// in RoboCert, variables are either RoboChart constants or spec-level bindings.
		return switch (v.getModifier()) {
			case CONST -> vx.constantId(v);
			case VAR -> bg.generateExpressionName(v);
		};
	}

	private CharSequence generateBinary(BinaryExpression it) {
		// TODO(@MattWindsor91): this can likely be optimised for precedence?
		return "(%s) %s (%s)".formatted(generate(it.getLeft()), generateOp(it), generate(it.getRight()));
	}

	private CharSequence generateOp(BinaryExpression it) {
		if (it instanceof And)
			return "and";
		if (it instanceof Or)
			return "or";
		if (it instanceof LessThan)
			return "<";
		if (it instanceof LessOrEqual)
			return "<=";
		if (it instanceof Equals)
			return "==";
		if (it instanceof Different)
			return "!=";
		if (it instanceof GreaterOrEqual)
			return ">=";
		if (it instanceof GreaterThan)
			return ">";
		throw new IllegalArgumentException("unsupported binary expression (only a few are supported so far): %s".formatted(it));
	}
}