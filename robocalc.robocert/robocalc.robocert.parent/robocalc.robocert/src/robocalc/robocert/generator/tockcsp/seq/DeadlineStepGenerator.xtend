package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.DeadlineStep
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator

/**
 * Generates CSP-M for deadlines.
 */
class DeadlineStepGenerator {
	@Inject CSPStructureGenerator csp
	@Inject extension ExpressionGenerator
	@Inject extension SubsequenceGenerator

	/**
	 * Generates CSP-M for a deadline step.
	 * 
	 * At the mathematical level, this becomes the 'deadline' tock-CSP
	 * operator.
	 * 
	 * @param it  the deadline step to generate.
	 * 
	 * @return the generated CSP.
	 */
	def generate(
		DeadlineStep it) '''«csp.function(DEADLINE_PROC, '''(«body.generate»)''', '''{- time units -} «units.generate»''')»'''

	/**
	 * Name of the process that implements the tick-tock deadline operator.
	 */
	static val DEADLINE_PROC = "EndBy" // in core_timed
}
