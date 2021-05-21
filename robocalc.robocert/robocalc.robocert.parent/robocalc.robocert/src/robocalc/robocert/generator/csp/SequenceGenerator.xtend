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
import robocalc.robocert.model.robocert.AnythingSequenceGap
import robocalc.robocert.model.robocert.SequenceGap
import robocalc.robocert.model.robocert.FinalSequenceAction
import robocalc.robocert.model.robocert.SequenceAction
import robocalc.robocert.model.robocert.EventSequenceArrow
import robocalc.robocert.model.robocert.OperationSequenceArrow
import robocalc.robocert.generator.ArrowDirection
import robocalc.robocert.generator.IndexedSequenceStep

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
	def String generate() {
		// NOTE: this might need factoring out into a sequential composition
		// rule.
		'''
			«sequence.name» = let
				«FOR step : IndexedSequenceStep.enumerate(sequence.steps)»
					Step«step.index» = «step.step.generateStep(step.index)»
				«ENDFOR»
			within Step0
		'''
	}

	/**
	 * @return CSP for the end of the sequence.
	 */
	def String generateEnd() {
		'''«IF sequence.isStrictEnd»SKIP«ELSE»«generateRun»«ENDIF»'''
	}

	/**
	 * @return CSP for any sequence of events tolerated by this sequence.
	 */
	def String generateRun() {
		// TODO: allow restricting Events
		"RUN(Events)"
	}

	/**
	 * @return generated CSP for one sequence step.
	 * 
	 * @param step   the step for which we are generating CSP.
	 * @param index  the index of the step.
	 */
	def String generateStep(SequenceStep step, int index) {
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
	def dispatch String generateAction(ArrowSequenceAction action, StrictSequenceGap gap, int index) {
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
	def dispatch String generateAction(ArrowSequenceAction action, AnythingSequenceGap gap, int index) {
		// TODO: allow negating events here
		// NOTE: if the arrow makes any bindings, we might not be able to
		// use throws?  An alternative encoding would be making the RUN exclude
		// the set of events the arrow can generate, and using /\.
		'''«gap.generateRun» [|«action.generateArrowEventSet»|> Step«index+1»'''
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
	def dispatch String generateAction(FinalSequenceAction action, SequenceGap gap, int index) {
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
	def dispatch String generateAction(SequenceAction action, SequenceGap gap, int index) {
		'''{- unsupported action: «action» (gap «gap» -} STOP'''
	}

	/** 
	 * @return the generated CSP for running the given gap.
	 *
	 * @param gap  the (strict) gap for which we are generating run.
	 */
	def dispatch String generateRun(StrictSequenceGap gap) {
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
	def dispatch String generateRun(AnythingSequenceGap gap) {
		// NOTE: will need something other than 'Events' here eventually
		"RUN(Events)"
	}

	/** 
	 * @return fallback CSP for an unknown gap type.
	 *
	 * @param gap  the gap for which we are generating run.
	 */
	def dispatch String generateRun(SequenceGap gap) {
		'''{- unknown gap: «gap» -} STOP'''
	}

	/**
	 * Generates a CSP event set for one sequence arrow.
	 * 
	 * @param arr  the arrow for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one sequence arrow.
	 */
	def String generateArrowEventSet(ArrowSequenceAction arr) {
		// NOTE: if the arrow is introducing a binding, this should be the
		// set of all possible communications on that arrow, not literally the
		// arrow's CSP elaboration.
		'''{| «arr.generateArrowPrefix» |}'''
	}

	/**
	 * Generates a CSP prefix for one sequence arrow action.
	 * 
	 * @param arr  the arrow for which we are generating CSP.
	 *
	 * @return generated CSP for one sequence arrow.
	 */
	def String generateArrowPrefix(ArrowSequenceAction arr) {
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
	def dispatch String generateArrow(EventSequenceArrow arr, ArrowDirection dir) {
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
	def dispatch String generateArrow(OperationSequenceArrow arr, ArrowDirection dir) {
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
	def dispatch String generateArrow(SequenceArrow arr, ArrowDirection dir) {
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
	def dispatch ArrowDirection getArrowDirection(PlatformSequenceActor from, ModuleSequenceActor to) {
		ArrowDirection::Input
	}

	/**
	 * @return output (to platform from module).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	def dispatch ArrowDirection getArrowDirection(ModuleSequenceActor from, PlatformSequenceActor to) {
		ArrowDirection::Output
	}

	/**
	 * @return unknown (fallback).
	 * 
	 * @param from  the from-actor.
	 * @param to    the to-actor.
	 */
	def dispatch ArrowDirection getArrowDirection(SequenceActor from, SequenceActor to) {
		ArrowDirection::Unknown
	}

	/**
	 * @return the namespace of all communications in this diagram.
	 */
	def String getNamespace() {
		// NOTE: it might not be the case that all communications have a common
		// namespace in future.
		sequence.target.namespace
	}

	/**
	 * @return the module name (as the namespace of any communications over the module).
	 * 
	 * @param target  the target for which we are getting a namespace.
	 */
	def dispatch String getNamespace(ModuleSequenceTarget target) {
		target.module.name
	}

	/**
	 * @return fallback for an unsupported sequence target
	 * 
	 * @param target  the target for which we are getting a namespace.
	 */
	def dispatch String getNamespace(SequenceTarget target) {
		"UNSUPPORTED_TARGET"
	}
}
