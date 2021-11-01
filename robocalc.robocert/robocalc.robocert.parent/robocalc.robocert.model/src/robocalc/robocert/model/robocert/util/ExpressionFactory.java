package robocalc.robocert.model.robocert.util;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.BoolExpr;
import robocalc.robocert.model.robocert.IntExpr;
import robocalc.robocert.model.robocert.RoboCertFactory;

/**
 * Helper factory that uses a {@link RoboCertFactory} to produce specific
 * types of expression.
 *
 * @author Matt Windsor
 */
public class ExpressionFactory {
	@Inject private RoboCertFactory rc;
	
	/**
	 * Creates a {@link BoolExpr} with the given truth value.
	 * 
	 * @param truth  the truth value.
	 * 
	 * @return  a RoboCert lifting of the given truth value.
	 */
	public BoolExpr bool(boolean truth) {
		var x = rc.createBoolExpr();
		x.setTruth(truth);
		return x;
	}
	
	/**
	 * Creates a {@link IntExpr} with the given value.
	 * 
	 * @param value  the value.
	 * 
	 * @return  a RoboCert lifting of the given integer value.
	 */
	public IntExpr integer(int value) {
		var x = rc.createIntExpr();
		x.setValue(value);
		return x;
	}	
}
