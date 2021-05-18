/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import robocalc.robocert.model.Sequence
import robocalc.robocert.model.Assertion
import robocalc.robocert.model.SequenceAssertionBody
import robocalc.robocert.model.AssertionBody
import robocalc.robocert.model.WitnessingSequenceAssertionBody
import robocalc.robocert.model.ModuleSequenceTarget
import robocalc.robocert.model.SequenceTarget
import robocalc.robocert.model.CSPFragment

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class RoboCertGenerator extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		fsa.generateFile('seq.csp', generate(resource));
	}

	/**
	 * @return generated CSP for all elements.
	 * 
	 * @param resource  the top-level property model.
	 */
	def String generate(Resource resource) {
		'''
			--
			-- CSP fragments
			--
			«generateCSPFragments(resource)»
			
			--
			-- Sequences
			--
			
			«generateSequences(resource)»
			
			--
			-- Assertions
			--
			
			«generateAssertions(resource)»
		'''
	}

	//
	// CSP fragments
	//
	/**
	 * @return included CSP for all raw CSP fragments.
	 * 
	 * @param resource  the top-level property model.
	 */
	def String generateCSPFragments(Resource resource) {
		// TODO: align this with RoboCert's process-based escape hatch.
		//
		// Currently our escape hatch is a lot more low-level, to let us
		// us sidestep issues in the generator as we bring up sequence
		// diagrams.  This will change later on.
		'''
			«FOR csp : resource.allContents.filter(CSPFragment).toIterable»
				«generateCSPFragment(csp)»
			«ENDFOR»
		'''
	}

	/**
	 * @return included CSP for a CSP fragment.
	 * 
	 * @param frag  the CSP fragment.
	 */
	def String generateCSPFragment(CSPFragment frag) {
		// stripping 'csp-begin' (9 chars) and 'csp-end' (7 chars).
		// TODO: is this the right way to do this, or do we need a value
		// converter?
		frag.contents.substring(9, frag.contents.length() - 7)
	}

	/**
	 * @return generated CSP for all sequences.
	 * 
	 * @param resource  the top-level property model.
	 */
	def String generateSequences(Resource resource) {
		'''
			«FOR seq : resource.allContents.filter(Sequence).toIterable»
				«generateSequence(seq)»
			«ENDFOR»
		'''
	}

	//
	// Sequences
	//
	/**
	 * @return generated CSP for one sequence.
	 * 
	 * @param seq  the sequence for which we are generating CSP.
	 */
	def String generateSequence(Sequence seq) {
		// TODO: emit correct CSP here.
		'''
			«seq.name» = let
				Step0 = SKIP
			within Step0
		'''
	}

	//
	// Assertions
	//
	/**
	 * @return generated CSP for all assertions.
	 * 
	 * @param resource  the top-level property model.
	 */
	def String generateAssertions(Resource resource) {
		'''
			«FOR asst : resource.allContents.filter(Assertion).toIterable»
				«generateAssertion(asst)»
			«ENDFOR»
		'''
	}

	/**
	 * @return generated CSP for one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def String generateAssertion(Assertion asst) {
		'''
			-- Assertion «asst.name»
			«generateAssertionBody(asst.body)»
		'''
	}

	/**
	 * @return generated CSP for one sequence assertion body.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def dispatch String generateAssertionBody(SequenceAssertionBody asst) {
		var lhs = generateAssertionLeft(asst);
		var rhs = generateAssertionRight(asst);
		var model = generateAssertionModel(asst);
		'''
			assert«IF asst.isNegated» not«ENDIF» «lhs» [«model»= «rhs»
		'''
	}

	/**
	 * Catch-all case for when we are asked to generate CSP for an assertion
	 * that can't have CSP generated for it.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * @return generated CSP for one sequence assertion body.
	 */
	def dispatch String generateAssertionBody(AssertionBody asst) {
		""
	}

	/**
	 * Generates CSP for the left-hand side of a witnessing assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	def dispatch String generateAssertionLeft(WitnessingSequenceAssertionBody asst) {
		generateAssertionSeqRef(asst)
	}

	/**
	 * Generates catch-all CSP for an unsupported assertion's left side.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	def dispatch String generateAssertionLeft(SequenceAssertionBody asst) {
		'''{- UNSUPPORTED LHS: «asst» -} STOP'''
	}

	/**
	 * Generates CSP for the right-hand side of a witnessing assertion.
	 * 
	 * Depending on the assertion type, this may expand to the sequence or the
	 * target of the sequence.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the right-hand side of the assertion.
	 */
	def dispatch String generateAssertionRight(WitnessingSequenceAssertionBody asst) {
		generateAssertionTarget(asst)
	}

	/**
	 * Generates catch-all CSP for an unsupported assertion's right side.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 * 
	 * @return generated CSP for the left-hand side of the assertion.
	 */
	def dispatch String generateAssertionRight(SequenceAssertionBody asst) {
		'''{- UNSUPPORTED RHS: «asst» -} STOP'''
	}

	/**
	 * @return generated CSP for a sequence reference in one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def String generateAssertionSeqRef(SequenceAssertionBody asst) {
		asst.sequence.name
	}

	/**
	 * @return generated CSP for the target of one assertion.
	 * 
	 * @param asst  the assertion for which we are generating CSP.
	 */
	def String generateAssertionTarget(SequenceAssertionBody asst) {
		generateTarget(asst.sequence.target)
	}

	/**
	 * @return generated CSP for a module sequence target.
	 * 
	 * @param tgt  the context for which we are generating CSP.
	 */
	def dispatch String generateTarget(ModuleSequenceTarget tgt) {
		// TODO: ideally this should get constant information from the
		// RoboChart metamodel, and inject user-defined values in.
		// Presumably the constant overriding should be per-assertion.
		'''P_«tgt.moduleName»'''
	}

	def dispatch String generateTarget(SequenceTarget tgt) {
		'''{- UNSUPPORTED TARGET: «tgt» -} STOP'''
	}

	/**
	 * @return the appropriate FDR model shorthand for this assertion.
	 */
	def String generateAssertionModel(AssertionBody asst) {
		switch asst.assertion.model {
			case TRACES:
				"T"
			case FAILURES:
				"F"
			case FAILURES_DIVERGENCES:
				"FD"
		}
	}
}
