package robocalc.robocert.generator.csp

import org.eclipse.emf.ecore.resource.Resource
import robocalc.robocert.model.robocert.CSPFragment
import robocalc.robocert.model.robocert.Assertion
import robocalc.robocert.model.robocert.Sequence
import com.google.inject.Inject

/**
 * Top-level generator for tock-CSP.
 */
class TopGenerator {
	@Inject extension AssertionGenerator
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
		
		«resource.generateSequences»
		
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
				«csp.generateCSPFragment»
			«ENDFOR»
		'''
	}

	/**
	 * @return included CSP for a CSP fragment.
	 * 
	 * @param frag  the CSP fragment.
	 */
	private def generateCSPFragment(CSPFragment frag) {
		// stripping 'csp-begin' (9 chars) and 'csp-end' (7 chars).
		// TODO: is this the right way to do this, or do we need a value
		// converter?
		frag.contents.substring(9, frag.contents.length - 7)
	}

	/**
	 * @return generated CSP for all sequences.
	 * 
	 * @param resource  the top-level property model.
	 */
	private def generateSequences(Resource resource) '''
		«FOR seq : resource.allContents.filter(Sequence).toIterable»
			«seq.generate»
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