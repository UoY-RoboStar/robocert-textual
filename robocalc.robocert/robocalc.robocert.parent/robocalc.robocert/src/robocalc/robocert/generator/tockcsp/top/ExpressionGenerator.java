package robocalc.robocert.generator.tockcsp.top;

import com.google.inject.Inject;
import robocalc.robocert.generator.utils.VariableExtensions;
import robocalc.robocert.model.robocert.IntExpr;
import robocalc.robocert.model.robocert.ConstExpr;
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.BindingExpr;
import robocalc.robocert.model.robocert.BoolExpr;

/**
 * The RoboCert expression generator.
 * 
 * RoboCert, for now, has a fairly simple expression language with a separate
 * generator from RoboChart.  This is predominantly to simplify dealing with
 * constants and parameter bindings.
 *
 * In the future, RoboCert may target the RoboChart language and/or generator.
 * It used to in the past, but this proved too complex.
 * 
 * @author Matt Windsor
 */
public class ExpressionGenerator {
	@Inject private BindingGenerator bg;
	@Inject private VariableExtensions vx;
	@Inject private UnsupportedSubclassHandler ush;

	/**
	 * Generates CSP-M for an expression.
	 * 
	 * @param it  the expression to generate.
	 * @return  CSP-M for the expression.
	 */
	public CharSequence generate(CertExpr it) {
		if (it instanceof BoolExpr b)
			return Boolean.toString(b.isTruth());
		if (it instanceof IntExpr i)
			return Integer.toString(i.getValue());
		if (it instanceof ConstExpr k)
			return vx.constantId(k.getConstant());
		if (it instanceof BindingExpr n && n.getSource() != null)
			return bg.generateExpressionName(n.getSource());
		return ush.unsupported(it, "expression", "0");
	}
}