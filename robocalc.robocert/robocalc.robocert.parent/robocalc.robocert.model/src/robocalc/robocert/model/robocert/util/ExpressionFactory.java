package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.Variable;
import com.google.inject.Inject;
import robocalc.robocert.model.robocert.BoolExpr;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.ConstExpr;
import robocalc.robocert.model.robocert.IntExpr;
import robocalc.robocert.model.robocert.LogicalExpr;
import robocalc.robocert.model.robocert.LogicalOperator;
import robocalc.robocert.model.robocert.MinusExpr;
import robocalc.robocert.model.robocert.RelationExpr;
import robocalc.robocert.model.robocert.RelationOperator;
import robocalc.robocert.model.robocert.RoboCertFactory;

/**
 * Helper factory that uses a {@link RoboCertFactory} to produce specific types of expression.
 *
 * @author Matt Windsor
 */
public class ExpressionFactory {
  @Inject private RoboCertFactory rc;

  /**
   * Creates a {@link BoolExpr} with the given truth value.
   *
   * @param truth the truth value.
   * @return a RoboCert lifting of the given truth value.
   */
  public BoolExpr bool(boolean truth) {
    final var x = rc.createBoolExpr();
    x.setTruth(truth);
    return x;
  }

  /**
   * Creates a {@link IntExpr} with the given value.
   *
   * @param value the value.
   * @return a RoboCert lifting of the given integer value.
   */
  public IntExpr integer(int value) {
    final var result = rc.createIntExpr();
    result.setValue(value);
    return result;
  }

  /**
   * Creates a {@link ConstExpr} with a given variable.
   *
   * @param v the variable.
   * @return the variable expression.
   */
  public ConstExpr var(Variable v) {
    final var result = rc.createConstExpr();
    result.setConstant(v);
    return result;
  }

  /**
   * Creates a {@link LogicalExpr} with the given operator and operands.
   *
   * @param op the operator.
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given relational expression.
   */
  public LogicalExpr logic(LogicalOperator op, CertExpr lhs, CertExpr rhs) {
    final var result = rc.createLogicalExpr();
    result.setOperator(op);
    result.setLhs(lhs);
    result.setRhs(rhs);
    return result;
  }

  /**
   * Creates a {@link RelationExpr} with the given operator and operands.
   *
   * @param op the operator.
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given relational expression.
   */
  public RelationExpr rel(RelationOperator op, CertExpr lhs, CertExpr rhs) {
    final var result = rc.createRelationExpr();
    result.setOperator(op);
    result.setLhs(lhs);
    result.setRhs(rhs);
    return result;
  }

  /**
   * Creates a {@link MinusExpr} with the given operand.
   *
   * @param e the operand.
   * @return the minus expression.
   */
  public MinusExpr minus(CertExpr e) {
    final var result = rc.createMinusExpr();
    result.setExpr(e);
    return result;
  }
}
