package robocalc.robocert.model.robocert.util;

import com.google.inject.Inject;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import robocalc.robocert.model.robocert.BindingExpr;
import robocalc.robocert.model.robocert.BoolExpr;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.ConstExpr;
import robocalc.robocert.model.robocert.IntExpr;
import robocalc.robocert.model.robocert.MinusExpr;
import robocalc.robocert.model.robocert.RelationExpr;
import robocalc.robocert.model.robocert.RelationOperator;
import robocalc.robocert.model.robocert.RoboCertFactory;

/**
 * Helper factory that uses a {@link RoboCertFactory} to produce specific types
 * of expression.
 *
 * @author Matt Windsor
 */
public class ExpressionFactory {
	@Inject
	private RoboCertFactory rc;
	@Inject
	private RoboChartFactory rchart;

	/**
	 * Creates a {@link BoolExpr} with the given truth value.
	 * 
	 * @param truth the truth value.
	 * 
	 * @return a RoboCert lifting of the given truth value.
	 */
	public BoolExpr bool(boolean truth) {
		var x = rc.createBoolExpr();
		x.setTruth(truth);
		return x;
	}

	/**
	 * Creates a {@link IntExpr} with the given value.
	 * 
	 * @param value the value.
	 * 
	 * @return a RoboCert lifting of the given integer value.
	 */
	public IntExpr integer(int value) {
		var result = rc.createIntExpr();
		result.setValue(value);
		return result;
	}
	
	/**
	 * Creates a {@link ConstExpr} with a dummy {@link Variable} which, in
	 * turn, has the given name.
	 *
	 * @param name the name of the variable.
	 * 
	 * @return the constant expression.
	 */
	public ConstExpr constant(String name) {
		var k = rchart.createVariable();
		k.setName(name);
		
		var result = rc.createConstExpr();
		result.setConstant(k);
		
		return result;
	}
	
	/**
	 * Creates a {@link Binding} with a dummy {@link Binding} which, in
	 * turn, has the given name.
	 *
	 * @param name the name of the variable.
	 * 
	 * @return the constant expression.
	 */
	public BindingExpr binding(String name) {
		var k = rc.createBinding();
		k.setName(name);
		
		var result = rc.createBindingExpr();
		result.setSource(k);
		
		return result;
	}

	/**
	 * Creates a {@link RelationExpr} with the given operator and operands.
	 * 
	 * @param op  the operator.
	 * @param lhs the left operand.
	 * @param rhs the right operand.
	 * 
	 * @return the given relational expression.
	 */
	public RelationExpr rel(RelationOperator op, CertExpr lhs, CertExpr rhs) {
		var result = rc.createRelationExpr();
		result.setOperator(op);
		result.setLhs(lhs);
		result.setRhs(rhs);
		return result;
	}
	
	/**
	 * Creates a {@link MinusExpr} with the given operand.
	 * 
	 * @param e the operand.
	 * 
	 * @return the minus expression.
	 */
	public MinusExpr minus(CertExpr e) {
		var result = rc.createMinusExpr();
		result.setExpr(e);
		return result;
	}
}
