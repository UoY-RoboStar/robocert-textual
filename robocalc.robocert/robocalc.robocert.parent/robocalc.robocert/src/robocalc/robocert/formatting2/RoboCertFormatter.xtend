/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.formatting2

import com.google.inject.Inject
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.services.RoboCertGrammarAccess

class RoboCertFormatter extends AbstractFormatter2 {
	
	@Inject extension RoboCertGrammarAccess

	def dispatch void format(robocalc.robocert.model.robocert.Package _package, extension IFormattableDocument document) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (sequence : _package.sequences) {
			sequence.format
		}
		for (assertion : _package.assertions) {
			assertion.format
		}
	}

	def dispatch void format(Sequence sequence, extension IFormattableDocument document) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		sequence.target.format
		for (sequenceActor : sequence.actors) {
			sequenceActor.format
		}
		for (sequenceStep : sequence.steps) {
			sequenceStep.format
		}
	}
	
	// TODO: implement for LooseSequenceStep, StrictSequenceStep, SequenceArrow
}
