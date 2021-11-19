package robocalc.robocert.generator.tockcsp.core;

import com.google.inject.Inject;

import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
import robocalc.robocert.generator.utils.VariableExtensions;
import robocalc.robocert.model.robocert.BinaryExpr;
import robocalc.robocert.model.robocert.BindingExpr;
import robocalc.robocert.model.robocert.BoolExpr;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.ConstExpr;
import robocalc.robocert.model.robocert.IntExpr;
import robocalc.robocert.model.robocert.LogicalExpr;
import robocalc.robocert.model.robocert.LogicalOperator;
import robocalc.robocert.model.robocert.MinusExpr;
import robocalc.robocert.model.robocert.RelationExpr;
import robocalc.robocert.model.robocert.RelationOperator;

/**
 * The RoboCert expression generator.
 *
 * RoboCert, for now, has a fairly simple expression language with a separate
 * generator from RoboChart. This is predominantly to simplify dealing with
 * constants and parameter bindings.
 *
 * In the future, RoboCert may target the RoboChart language and/or generator.
 * It used to in the past, but this proved too complex.
 *
 * @author Matt Windsor
 */
public class ExpressionGenerator {
	@Inject
	private BindingGenerator bg;
	@Inject
	private VariableExtensions vx;
	@Inject
	private UnsupportedSubclassHandler ush;

	/**
	 * Generates CSP-M for an expression.
	 *
	 * @param it the expression to generate.
	 * @return CSP-M for the expression.
	 */
	public CharSequence generate(CertExpr it) {
		// TODO(@MattWindsor91): replace this with a type switch when it stops
		// being a preview feature. I tried using it as a preview feature, but
		// couldn't get the UI to load under preview mode.
		if (it instanceof BinaryExpr b)
			return generateBinary(b);
		if (it instanceof BindingExpr n && n.getSource() != null)
			return bg.generateExpressionName(n.getSource());
		if (it instanceof BoolExpr b)
			return Boolean.toString(b.isTruth());
		if (it instanceof ConstExpr k)
			return vx.constantId(k.getConstant());
		if (it instanceof IntExpr i)
			return Integer.toString(i.getValue());
		if (it instanceof MinusExpr m)
			return "-(" + generate(m.getExpr()) + ")";
		return ush.unsupported(it, "expression", "0");
	}

	private CharSequence generateBinary(BinaryExpr it) {
		// TODO(@MattWindsor91): this can likely be optimised for precedence,
		// but that might be a waste of effort that could instead be used
		// merging this and RoboChart's expression language?
		return "(%s) %s (%s)".formatted(generate(it.getLhs()), generateOp(it), generate(it.getRhs()));
	}

	private CharSequence generateOp(BinaryExpr it) {
		if (it instanceof LogicalExpr l)
			return generateLogicalOp(l.getOperator());
		if (it instanceof RelationExpr r)
			return generateRelationOp(r.getOperator());
		return ush.unsupported(it, "operator", "+");
	}

	private CharSequence generateLogicalOp(LogicalOperator it) {
		// At time of writing, these are NOT the same in CSP-M and RoboCert.
		return switch (it) {
		case AND -> "and";
		case OR -> "or";
		};
	}

	private CharSequence generateRelationOp(RelationOperator it) {
		// At time of writing, these are the same in CSP-M and RoboCert.
		return switch (it) {
		case LT -> "<";
		case LE -> "<=";
		case EQ -> "==";
		case NE -> "!=";
		case GE -> ">=";
		case GT -> ">";
		};
	}
}