package robocalc.robocert.generator.tockcsp.top

import org.eclipse.emf.ecore.resource.Resource
import robocalc.robocert.model.robocert.CSPFragment
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.model.robocert.SequenceGroup
import com.google.inject.Inject
import robocalc.robocert.generator.tockcsp.seq.SequenceGenerator

/**
 * Top-level generator for tock-CSP.
 */
class TopGenerator {
	@Inject extension AssertionGenerator
	@Inject extension CSPFragmentGenerator
	@Inject extension ImportGenerator
	@Inject extension SequenceGenerator
	
	/**
	 * @return generated CSP for all elements.
	 * 
	 * @param resource  the top-level property model.
	 */
	def CharSequence generate(Resource resource) '''
		«resource.generateImports»

		--
		-- CSP fragments
		--
		
		«resource.generateCSPFragments»
		
		--
		-- Sequences
		--
		
		«resource.generateSequenceGroups»
		
		--
		-- Assertions
		--
		
		«resource.generateAssertions»
	'''

	//
	// CSP fragments
	//
	/**
	 * @return included CSP for all raw CSP fragments.
	 * 
	 * @param resource  the top-level property model.
	 */
	private def generateCSPFragments(Resource resource) {
		// TODO: align this with RoboCert's process-based escape hatch.
		//
		// Currently our escape hatch is a lot more low-level, to let us
		// us sidestep issues in the generator as we bring up sequence
		// diagrams.  This will change later on.
		'''
			«FOR csp : resource.allContents.filter(CSPFragment).toIterable»
				«csp.generate»
			«ENDFOR»
		'''
	}

	/**
	 * @return generated CSP for all sequences.
	 * 
	 * @param resource  the top-level property model.
	 */
	private def generateSequenceGroups(Resource resource) '''
		«FOR group : resource.allContents.filter(SequenceGroup).toIterable»
			«group.generateGroup»
		«ENDFOR»
	'''

	//
	// Assertions
	//
	/**
	 * @return generated CSP for all assertions.
	 * 
	 * @param resource  the top-level property model.
	 */
	private def generateAssertions(Resource resource) '''
		«FOR asst : resource.allContents.filter(Assertion).toIterable»
			«asst.generate»
		«ENDFOR»
	'''
}