package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.model.robocert.InfiniteLoopBound
import robocalc.robocert.model.robocert.LowerLoopBound
import robocalc.robocert.model.robocert.DefiniteLoopBound
import robocalc.robocert.model.robocert.RangeLoopBound
import robocalc.robocert.model.robocert.LoopStep
import robocalc.robocert.model.robocert.LoopBound
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator

/**
 * Generates CSP for loops.
 * 
 * Most of this CSP is calls into either the CSP-M or RoboCert standard
 * libraries.
 */
class LoopGenerator {
	@Inject extension LoadStoreGenerator
	@Inject extension ExpressionGenerator
	@Inject extension SubsequenceGenerator
	
	/**
	 * Generates CSP for a loop action.
	 * 
	 * @param it  the loop action to generate.
	 * 
	 * @return the generated CSP.
	 */
	def generateLoop(LoopStep it) '''
	«bound.generateExpressionLoads»«bound.generateBound»(
		«body.generate»
	)
	'''
	
	/**
	 * Expands to the appropriate stock process for an infinite loop.
	 *
	 * This is just the CSPM 'loop' process.
	 * 
	 * @param it  the loop bound.
	 * 
	 * @return the parametric process to be instantiated with the
	 *         process-to-loop to yield the appropriate loop.
	 */
	def private dispatch generateBound(InfiniteLoopBound it) '''loop'''

	/**
	 * Expands to the appropriate stock process for a lower-bounded loop.
	 * 
	 * @param it  the loop bound.
	 * 
	 * @return the parametric process to be instantiated with the
	 *         process-to-loop to yield the appropriate loop.
	 */
	def private dispatch generateBound(LowerLoopBound it) '''loop_at_least(«lowerTimes.generate»)'''
	
	/**
	 * Expands to the appropriate stock process for a definite-bounded loop.
	 *
	 * This is just the CSPM 'loop' process.
	 * 
	 * @param it  the loop bound.
	 * 
	 * @return the parametric process to be instantiated with the
	 *         process-to-loop to yield the appropriate loop.
	 */
	def private dispatch generateBound(DefiniteLoopBound it) '''loop_exactly(«times.generate»)'''
		
	/**
	 * Expands to the appropriate stock process for a range-bounded loop.
	 *
	 * This is just the CSPM 'loop' process.
	 * 
	 * @param it  the loop bound.
	 * 
	 * @return the parametric process to be instantiated with the
	 *         process-to-loop to yield the appropriate loop.
	 */
	def private dispatch generateBound(RangeLoopBound it) '''loop_between(«lowerTimes.generate», «upperTimes.generate»)'''
	
	/**
	 * Fallback for an unknown loop bound.
	 *
	 * This is just the CSPM 'loop' process.
	 * 
	 * @param it  the loop bound.
	 * 
	 * @return the parametric process to be instantiated with the
	 *         process-to-loop to yield the appropriate loop.
	 */
	def private dispatch generateBound(LoopBound it) '''{- UNKNOWN BOUND: «it» -}loop'''	
}
