package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.RCModuleTarget
import robocalc.robocert.model.robocert.Actor
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.SequenceStep
import robocalc.robocert.model.robocert.ArrowAction
import robocalc.robocert.model.robocert.StrictGap
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.FinalAction
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.generator.ArrowDirection
import robocalc.robocert.model.robocert.LooseGap
import robocalc.robocert.model.robocert.GapMessageSet
import robocalc.robocert.model.robocert.World
import robocalc.robocert.model.robocert.Target
import com.google.inject.Inject

/**
 * A generator that emits untimed CSP for a sequence.
 */
class SequenceGenerator {
	@Inject extension TopicGenerator tg
	
	// TODO: handle timed vs untimed CSP
	// TODO: consider moving some of the extension methods into the model
	/**
	 * Generates CSP for a sequence.
	 * 
	 * @param sequence  the sequence for which we are generating CSP.
	 * 
	 * @return CSP for this generator's sequence.
	 */
	def CharSequence generateSequence(Sequence sequence) '''
		«sequence.name» =
			«sequence.steps.generateSteps»
	'''

	/**
	 * Generates CSP for a sequential composition of steps.
	 * 
	 * @param steps   the step set for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence step.
	 */
	private def generateSteps(Iterable<SequenceStep> steps) '''
		«FOR step : steps SEPARATOR ';'»
			«step.generateStep»
		«ENDFOR»
	'''

	/**
	 * @return generated CSP for one sequence step.
	 * 
	 * @param step   the step for which we are generating CSP.
	 */
	private def generateStep(SequenceStep step) {
		step.action.generateAction(step.gap)
	}

	/**
	 * Generates CSP for an arrow action with a strict gap.
	 * 
	 * @param action  the arrow action.
	 * @param gap     the strict gap.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(ArrowAction action, StrictGap gap) {
		// NOTE: always leading to StepN+1 might need changing when we
		// introduce combined fragments.
		'''(«action.body.generateSpecPrefix» -> SKIP)'''
	}

	/**
	 * Generates CSP for an arrow action with an anything gap.
	 * 
	 * @param action  the arrow action.
	 * @param gap     the anything gap.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(ArrowAction action, LooseGap gap) {
		// TODO: allow negating events here
		// NOTE: if the arrow makes any bindings, we might not be able to
		// use throws?  An alternative encoding would be making the RUN exclude
		// the set of events the arrow can generate, and using /\.
		'''(«gap.generateRun» [|{|«action.body.generateEventSet»|}|> SKIP)'''
	}

	/**
	 * Generates CSP for a final action.
	 * 
	 * @param action  the final action.
	 * @param gap     the gap.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(FinalAction action, SequenceGap gap) {
		gap.generateRun
	}

	/**
	 * Generates fallback CSP for an unsupported action.
	 * 
	 * @param action  the arrow action.
	 * @param gap     the (strict) gap.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(SequenceAction action,
		SequenceGap gap) '''{- unsupported action: «action» (gap «gap» -} STOP'''

	/** 
	 * @return the generated CSP for running the given gap.
	 * 
	 * @param gap  the (strict) gap for which we are generating run.
	 */
	private def dispatch generateRun(StrictGap gap) {
		// A strict gap doesn't allow anything to happen until something we
		// want to happen *does* happen, so if there isn't such a thing then
		// we assume termination.
		// TODO: this doesn't seem right, strictly speaking.
		"SKIP"
	}

	/** 
	 * @return the generated CSP for running the given gap.
	 * 
	 * @param gap  the (anything) gap for which we are generating run.
	 */
	private def dispatch generateRun(LooseGap gap) {
		// We consider empty allowed-sets to allow everything.
		val allow = gap.allowed.present ? gap.allowed.generateSet : "Events";
		// NOTE: if we need to implement gapped prefixes using /\,
		// we'll need to add the prefix's set to the forbidden set.
		'''RUN(«IF gap.forbidden.present»diff(«allow», «gap.forbidden.generateSet»)«ELSE»«allow»«ENDIF»)'''
	}

	/** 
	 * @return fallback CSP for an unknown gap type.
	 * 
	 * @param gap  the gap for which we are generating run.
	 */
	private def dispatch generateRun(SequenceGap gap) '''{- unknown gap: «gap» -} STOP'''

	/**
	 * Generates a CSP event set for a gap message set (less the set delimiters).
	 * 
	 * @param set  the message set for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set for multiple sequence messages.
	 */
	private def generateSet(
		GapMessageSet set) '''{|«FOR m : set.messages SEPARATOR ', '»«m.generateEventSet»«ENDFOR»|}'''

	/**
	 * @return whether this message set is present (non-null and has messages).
	 */
	private def isPresent(GapMessageSet set) {
		// TODO: check if this is actually necessary
		// TODO: move to metamodel?
		set !== null && !set.messages.isNullOrEmpty
	}

	/**
	 * Generates a CSP event set for one message spec (less the set delimiters).
	 * 
	 * @param spec  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one message spec.
	 */
	private def generateEventSet(MessageSpec spec) {
		// NOTE: if this is an arrow introducing a binding, this should be the
		// set of all possible communications on that arrow, not literally the
		// arrow's CSP elaboration.
		'''«spec.generateSpecPrefix»'''
	}

	/**
	 * Generates a CSP prefix for one sequence arrow action.
	 * 
	 * @param spec  the spec for which we are generating CSP.
	 * 
	 * @return generated CSP for the message spec.
	 */
	private def generateSpecPrefix(MessageSpec spec) {
		// NOTE: we might need to consider from/to at a more sophisticated
		// level than just boiling them down to 'in'/'out' eventually.
		spec.topic.generate(getSpecDirection(spec.from, spec.to), getNamespaceFromPair(spec.from, spec.to))
	}


	//
	// Message directions
	//
	// Currently we assume there are only two actors in the sequence diagram,
	// and we're assuming that one is the platform and one is the module,
	// so we just do a type switch on those actors to work out which is which
	// and eventually resolve it to 'out' or 'in' in the CSP.
	//
	/**
	 * @return input (from world to target).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch getSpecDirection(World from, Target to) {
		ArrowDirection::Input
	}

	/**
	 * @return output (to platform from module).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch getSpecDirection(Target from, World to) {
		ArrowDirection::Output
	}

	/**
	 * @return unknown (fallback).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch getSpecDirection(Actor from, Actor to) {
		ArrowDirection::Unknown
	}

	/**
	 * Gets the sequence's namespace by trying to find the target in a pair of
	 * actors.
	 * 
	 * @param from  the from-node of the arrow.
	 * @param to    the to-node of the arrow.
	 * 
	 * @return the sequence's namespace.
	 */
	private def getNamespaceFromPair(Actor from, Actor to) {
		var it = from.namespace
		if(empty) it = to.namespace
		if(empty) it = "UNSUPPORTED_ACTORS"
		it
	}

	/**
	 * Scrapes the namespace from a RoboChart module.
	 * 
	 * @param actor  the actor for which we are getting a namespace.
	 * @return the module name (as the namespace of any communications over the module).

	 */
	private def dispatch getNamespace(RCModuleTarget target) {
		target.module.name
	}

	/**
	 * Fallback for actors that don't correspond to a namespace.
	 * @param actor  the target for which we are getting a namespace.
	 * @return the empty string (signifying this actor has no namespace).
	 */
	private def dispatch getNamespace(Actor target) {
		""
	}
}
