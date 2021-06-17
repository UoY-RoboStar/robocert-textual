package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.generator.utils.TargetExtensions
import robocalc.robocert.utils.MessageAnalysis

/**
 * Generates CSP for various aspects of message specs.
 */
class MessageSpecGenerator {
	@Inject extension TopicGenerator

	/**
	 * Generates a CSP event set for one message spec (less the set delimiters).
	 * 
	 * @param spec  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one message spec.
	 */
	def generateCSPEventSet(MessageSpec spec) {
		// NOTE: if this is an arrow introducing a binding, this should be the
		// set of all possible communications on that arrow, not literally the
		// arrow's CSP elaboration.
		'''«spec.generatePrefix»'''
	}

	/**
	 * Generates a CSP prefix for one sequence arrow action.
	 * 
	 * @param spec  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the message spec.
	 */
	def generatePrefix(MessageSpec spec) {
		// NOTE: we might need to consider from/to at a more sophisticated
		// level than just boiling them down to 'in'/'out' eventually.
		spec.topic.generate(MessageAnalysis.analyse(spec))
	}
}
