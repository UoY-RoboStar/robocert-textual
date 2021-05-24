package robocalc.robocert.generator.csp

import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.PlatformSequenceActor
import robocalc.robocert.model.robocert.ModuleSequenceActor
import robocalc.robocert.model.robocert.SequenceActor
import robocalc.robocert.model.robocert.SequenceArrow
import robocalc.robocert.model.robocert.ModuleSequenceTarget
import robocalc.robocert.model.robocert.SequenceTarget
import robocalc.robocert.model.robocert.SequenceStep
import robocalc.robocert.model.robocert.ArrowSequenceAction
import robocalc.robocert.model.robocert.StrictSequenceGap
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.FinalSequenceAction
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.EventSequenceArrow
import robocalc.robocert.model.robocert.OperationSequenceArrow
import robocalc.robocert.generator.ArrowDirection
import robocalc.robocert.generator.IndexedSequenceStep
import robocalc.robocert.model.robocert.LooseSequenceGap
import robocalc.robocert.model.robocert.ArrowSet

/**
 * A generator that emits untimed CSP for a sequence.
 */
class SequenceGenerator {
	// TODO: handle timed vs untimed CSP
	// TODO: consider moving some of the extension methods into the model
	/**
	 * Reference to the top-level sequence.
	 */
	Sequence sequence;

	/**
	 * Constructs a CSP generator for a given sequence.
	 * 
	 * @param sequence  the sequence for which we are generating CSP.
	 */
	new(Sequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return CSP for this generator's sequence.
	 */
	def CharSequence generate() // NOTE: might need factoring into a sequential composition rule.
	'''
		«sequence.name» = let
			«FOR step : IndexedSequenceStep.enumerate(sequence.steps)»
				Step«step.index» = «step.step.generateStep(step.index)»
			«ENDFOR»
		within Step0
	'''

	/**
	 * @return generated CSP for one sequence step.
	 * 
	 * @param step   the step for which we are generating CSP.
	 * @param index  the index of the step.
	 */
	private def generateStep(SequenceStep step, int index) {
		step.action.generateAction(step.gap, index)
	}

	/**
	 * Generates CSP for an arrow action with a strict gap.
	 * 
	 * @param action  the arrow action.
	 * @param gap     the strict gap.
	 * @param index   the index of the step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(ArrowSequenceAction action, StrictSequenceGap gap, int index) {
		// NOTE: always leading to StepN+1 might need changing when we
		// introduce combined fragments.
		'''«action.generateArrowPrefix» -> Step«index+1»'''
	}

	/**
	 * Generates CSP for an arrow action with an anything gap.
	 * 
	 * @param action  the arrow action.
	 * @param gap     the anything gap.
	 * @param index   the index of the step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(ArrowSequenceAction action, LooseSequenceGap gap, int index) {
		// TODO: allow negating events here
		// NOTE: if the arrow makes any bindings, we might not be able to
		// use throws?  An alternative encoding would be making the RUN exclude
		// the set of events the arrow can generate, and using /\.
		'''«gap.generateRun» [|{|«action.generateArrowEventSet»|}|> Step«index+1»'''
	}

	/**
	 * Generates CSP for a final action.
	 * 
	 * @param action  the final action.
	 * @param gap     the gap.
	 * @param index   the index of the step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(FinalSequenceAction action, SequenceGap gap, int index) {
		gap.generateRun
	}

	/**
	 * Generates fallback CSP for an unsupported action.
	 * 
	 * @param action  the arrow action.
	 * @param gap     the (strict) gap.
	 * @param index   the index of the step.
	 * 
	 * @return the generated CSP.
	 */
	private def dispatch generateAction(SequenceAction action, SequenceGap gap,
		int index) '''{- unsupported action: «action» (gap «gap» -} STOP'''

	/** 
	 * @return the generated CSP for running the given gap.
	 * 
	 * @param gap  the (strict) gap for which we are generating run.
	 */
	private def dispatch generateRun(StrictSequenceGap gap) {
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
	private def dispatch generateRun(LooseSequenceGap gap) {
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
	 * Generates a CSP event set for a set of arrows (less the set delimiters).
	 * 
	 * @param set  the arrow set for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set for multiple sequence arrows.
	 */
	private def generateSet(ArrowSet set) '''{|«FOR arr : set.arrows SEPARATOR ', '»«arr.generateArrowEventSet»«ENDFOR»|}'''

	private def isPresent(ArrowSet set) {
		// TODO: check if this is actually necessary
		// TODO: move to metamodel?
		set !== null && !set.arrows.isNullOrEmpty
	}

	/**
	 * Generates a CSP event set for one sequence arrow (less the set delimiters).
	 * 
	 * @param arr  the arrow for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one sequence arrow.
	 */
	private def generateArrowEventSet(ArrowSequenceAction arr) {
		// NOTE: if the arrow is introducing a binding, this should be the
		// set of all possible communications on that arrow, not literally the
		// arrow's CSP elaboration.
		'''«arr.generateArrowPrefix»'''
	}

	/**
	 * Generates a CSP prefix for one sequence arrow action.
	 * 
	 * @param arr  the arrow for which we are generating CSP.
	 * 
	 * @return generated CSP for one sequence arrow.
	 */
	private def generateArrowPrefix(ArrowSequenceAction arr) {
		// NOTE: we might need to consider from/to at a more sophisticated
		// level than just boiling them down to 'in'/'out' eventually.
		arr.body.generateArrow(getArrowDirection(arr.from, arr.to))
	}

	/**
	 * @return generated CSP for an event arrow.
	 * 
	 * @param arr  the arrow body for which we are generating CSP.
	 * @param dir  the direction of the arrow.
	 */
	private def dispatch generateArrow(EventSequenceArrow arr, ArrowDirection dir) {
		// TODO: parameters
		// NOTE: parameters might eventually introduce bindings
		'''«namespace»::«arr.event.name».«dir»'''
	}

	/**
	 * @return generated CSP for an operation call.
	 * 
	 * @param arr  the arrow body for which we are generating CSP.
	 * @param dir  the direction of the arrow.
	 */
	private def dispatch generateArrow(OperationSequenceArrow arr, ArrowDirection dir) {
		// TODO: parameters
		// NOTE: parameters might eventually introduce bindings		
		'''«namespace»::«arr.operation.name»Call'''
	}

	/**
	 * Fallback for generating an arrow body when we don't recognise the actors
	 * and body combination.
	 * 
	 * Getting here suggests validation isn't working properly.
	 * 
	 * @param arr   the arrow body.
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch generateArrow(SequenceArrow arr, ArrowDirection dir) {
		'''{- unsupported arrow body: arr=«arr» dir=«dir» -} tock'''
	}

	//
	// Arrow directions
	//
	// Currently we assume there are only two actors in the sequence diagram,
	// and we're assuming that one is the platform and one is the module,
	// so we just do a type switch on those actors to work out which is which
	// and eventually resolve it to 'out' or 'in' in the CSP.
	//
	/**
	 * @return input (from platform to module).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch getArrowDirection(PlatformSequenceActor from, ModuleSequenceActor to) {
		ArrowDirection::Input
	}

	/**
	 * @return output (to platform from module).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch ArrowDirection getArrowDirection(ModuleSequenceActor from, PlatformSequenceActor to) {
		ArrowDirection::Output
	}

	/**
	 * @return unknown (fallback).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	private def dispatch ArrowDirection getArrowDirection(SequenceActor from, SequenceActor to) {
		ArrowDirection::Unknown
	}

	/**
	 * @return the namespace of all communications in this diagram.
	 */
	private def getNamespace() {
		// NOTE: it might not be the case that all communications have a common
		// namespace in future.
		sequence.target.namespace
	}

	/**
	 * @return the module name (as the namespace of any communications over the module).
	 * 
	 * @param target  the target for which we are getting a namespace.
	 */
	private def dispatch getNamespace(ModuleSequenceTarget target) {
		target.module.name
	}

	/**
	 * @return fallback for an unsupported sequence target
	 * 
	 * @param target  the target for which we are getting a namespace.
	 */
	private def dispatch getNamespace(SequenceTarget target) {
		"UNSUPPORTED_TARGET"
	}
}
