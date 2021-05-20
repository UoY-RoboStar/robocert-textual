package robocalc.robocert.generator

import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.StrictSequenceStep
import robocalc.robocert.model.robocert.PlatformSequenceActor
import robocalc.robocert.model.robocert.ModuleSequenceActor
import robocalc.robocert.model.robocert.SequenceArrowBody
import robocalc.robocert.model.robocert.SequenceActor
import robocalc.robocert.model.robocert.EventSequenceArrowBody
import robocalc.robocert.model.robocert.SequenceArrow
import robocalc.robocert.model.robocert.LooseSequenceStep
import robocalc.robocert.model.robocert.ModuleSequenceTarget
import robocalc.robocert.model.robocert.SequenceTarget
import robocalc.robocert.model.robocert.OperationSequenceArrowBody

/**
 * A generator that emits (untimed, for now) CSP for a sequence.
 */
class SequenceCSPGenerator {
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
				Step«sequence.steps.length» = «generateEnd»
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
	 * @return generated CSP for one strict sequence step.
	 * 
	 * @param step   the step for which we are generating CSP.
	 * @param index  the index of the step.
	 */
	def dispatch String generateStep(StrictSequenceStep step, int index) {
		// NOTE: always leading to StepN+1 might need changing when we
		// introduce combined fragments.
		'''«step.arrow.generateArrow» -> Step«index+1»'''
	}

	/**
	 * @return generated CSP for one loose sequence step.
	 * 
	 * A loose sequence step becomes a throws operator, where the arrow is the
	 * event being thrown on.
	 * 
	 * @param step   the step for which we are generating CSP.
	 * @param index  the index of the step.
	 */
	def dispatch String generateStep(LooseSequenceStep step, int index) {
		// TODO: allow negating events here
		// NOTE: if the arrow makes any bindings, we might not be able to
		// use throws?  An alternative encoding would be making the RUN exclude
		// the set of events the arrow can generate, and using /\.
		'''«generateRun» [|«step.arrow.generateArrowEventSet»|> Step«index+1»'''
	}
	
	/**
	 * Generates a CSP event set for one sequence arrow.
	 * 
	 * @param arr  the arrow for which we are generating CSP.
	 * 
	 * @return generated CSP for the event set of one sequence arrow.
	 */
	def String generateArrowEventSet(SequenceArrow arr) {
		// NOTE: if the arrow is introducing a binding, this should be the
		// set of all possible communications on that arrow, not literally the
		// arrow's CSP elaboration.
		'''{| «arr.generateArrow» |}'''
	}

	/**
	 * Generates a CSP event for one sequence arrow.
	 * 
	 * @param arr  the arrow for which we are generating CSP.
	 *
	 * @return generated CSP for one sequence arrow.
	 */
	def String generateArrow(SequenceArrow arr) {
		// NOTE: we might need to consider from/to at a more sophisticated
		// level than just boiling them down to 'in'/'out' eventually.
		arr.body.generateArrowBody(getArrowDirection(arr.from, arr.to))
	}

	/**
	 * @return generated CSP for an event arrow.
	 * 
	 * @param arr  the arrow body for which we are generating CSP.
	 * @param dir  the direction of the arrow.
	 */
	def dispatch String generateArrowBody(EventSequenceArrowBody arr, ArrowDirection dir) {
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
	def dispatch String generateArrowBody(OperationSequenceArrowBody arr, ArrowDirection dir) {
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
	def dispatch String generateArrowBody(SequenceArrowBody arr, ArrowDirection dir) {
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
