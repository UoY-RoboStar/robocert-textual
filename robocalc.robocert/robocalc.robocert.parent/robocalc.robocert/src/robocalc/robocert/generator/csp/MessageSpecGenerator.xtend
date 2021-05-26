package robocalc.robocert.generator.csp

import com.google.inject.Inject
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.generator.ArrowDirection
import robocalc.robocert.model.robocert.World
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.Actor
import robocalc.robocert.model.robocert.RCModuleTarget

/**
 * Generates CSP for various aspects of message specs.
 */
class MessageSpecGenerator {
	@Inject extension TopicGenerator tg

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
	private def dispatch String getNamespace(RCModuleTarget target) {
		target.module.name
	}

	/**
	 * Fallback for actors that don't correspond to a namespace.
	 * @param actor  the target for which we are getting a namespace.
	 * @return the empty string (signifying this actor has no namespace).
	 */
	private def dispatch String getNamespace(Actor target) ''''''
}