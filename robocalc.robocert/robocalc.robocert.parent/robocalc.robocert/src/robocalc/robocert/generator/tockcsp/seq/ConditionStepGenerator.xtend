package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.model.robocert.ConditionStep
import com.google.inject.Inject

/**
 * Generates CSP-M for ConditionSteps.
 */
class ConditionStepGenerator {
	@Inject extension ExpressionGenerator
	
	def generate(ConditionStep it) '''(«condition.generate» & SKIP)'''
}