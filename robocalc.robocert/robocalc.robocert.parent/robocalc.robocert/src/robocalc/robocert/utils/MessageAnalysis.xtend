package robocalc.robocert.utils

import robocalc.robocert.model.robocert.Target
import robocalc.robocert.utils.ArrowDirection
import robocalc.robocert.model.robocert.MessageSpec
import org.eclipse.xtend.lib.annotations.Data

/**
 * An analysis of a message specification.
 */
@Data class MessageAnalysis {
	/**
	 * The target, if any, referenced in this message.
	 */
	Target target;
	/**
	 * The direction of this message with respect to the target.
	 */
	ArrowDirection direction;
	
	/**
	 * Analyses a specification, producing a message analysis.
	 * 
	 * @param it  the specification to analyse.
	 * 
	 * @return  a message analysis.
	 */
	def static analyse(MessageSpec it) {
		switch t: from {
			Target: {
				return new MessageAnalysis(t, ArrowDirection::Output)
			}
		}
		switch t: to {
			Target: {
				return new MessageAnalysis(t, ArrowDirection::Input)
			}
		}
		new MessageAnalysis(null, ArrowDirection::Unknown)
	}
	
	/**
	 * Gets the channel name in the CSP semantics for the direction.
	 * 
	 * @return  "in" for input, "out" for output, unspecified otherwise.
	 */
	def cspDirection() {
		switch direction {
			case Input: "in"
			case Output: "out"
			case Unknown: "UNKNOWN"
		}
	}
}