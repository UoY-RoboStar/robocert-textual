/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.tests.util

import robocalc.robocert.model.robocert.Actor
import robocalc.robocert.model.robocert.Argument
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.model.robocert.MessageSpec
import robocalc.robocert.model.robocert.MessageTopic
import robocalc.robocert.model.robocert.RoboCertFactory
import circus.robocalc.robochart.RoboChartFactory
import circus.robocalc.robochart.Event
import com.google.inject.Inject
import static extension org.junit.jupiter.api.Assertions.*
import robocalc.robocert.model.robocert.MessageSet
import robocalc.robocert.model.robocert.WildcardArgument
import robocalc.robocert.model.robocert.util.MessageFactory
import circus.robocalc.robochart.OperationSig
import robocalc.robocert.model.robocert.TargetActorRelationship

/**
 * Provides ways of creating dummy message specifications.
 */
class MessageSpecFactory {
	// TODO(@MattWindsor91): reduce overlap with model MessageFactory;
	// the idea is that that will receive non-dummy factory operations.
	
	@Inject MessageFactory mf;
	@Inject RoboChartFactory rc;
	@Inject RoboCertFactory rcert;

	/**
	 * Creates an arrow message spec with the given topic, direction, and
	 * arguments, with a fake sequence-group context.
	 * 
	 * @param t     the desired topic.
	 * @param dir   the desired direction.
	 * @param args  the desired arguments.
	 * 
	 * @return a constructed arrow message spec.
	 */
	def MessageSpec arrowSpec(MessageTopic t, MessageDirection dir, Argument... args) {
		mf.spec(t, mf.directional(dir), args) => [s|arrowParent.body = s]
	}

	def private arrowParent() {
		rcert.createArrowAction => [
			step = rcert.createActionStep => [
				gap = rcert.createExtensionalMessageSet
				parent = sseq
			]
		]
	}

	/**
	 * Creates a gap message spec with the given topic, direction, and
	 * arguments, with a fake sequence-group context.
	 * 
	 * @param t     the desired topic.
	 * @param dir   the desired direction.
	 * @param args  the desired arguments.
	 * 
	 * @return a constructed gap message spec.
	 */
	def MessageSpec gapSpec(MessageTopic t, MessageDirection dir, Argument... args) {
		mf.spec(t, mf.directional(dir), args) => [s|gapParent.messages.add(s)]
	}

	/**
	 * Sets up a gap message spec parent that has enough context for a
	 * sequence group to be located.
	 */
	def gapParent() {
		rcert.createExtensionalMessageSet => [ g | g.setupAsGap ]
	}

	/**
	 * Hoists the given set into being the gap set for an action step that
	 * is attached to the test subsequence.
	 * 
	 * Acts in-place.
	 * 
	 * @param g  the set to hoist.
	 */
	def setupAsGap(MessageSet g) {
		rcert.createActionStep => [
			gap = g
			parent = sseq
		]		
	}

	def Argument intArg(int v) {
		rcert.createExpressionArgument => [
			expr = rcert.createIntExpr => [
				value = v
			]
		]
	}

	/**
	 * Shorthand for creating a wildcard argument.
	 * 
	 * @return  a wildcard argument.
	 */
	def WildcardArgument wildcardArg() {
		rcert.createWildcardArgument
	}

	/**
	 * Shorthand for creating a bound argument with the given name.
	 * 
	 * @param n  the name to bind to.
	 * 
	 * @return  a bound argument.
	 */
	def WildcardArgument boundArg(String n) {
		rcert.createWildcardArgument => [binding = rcert.createBinding => [name = n]]
	}

	def Event intEvent() {
		rc.createEvent => [
			name = "event"
			type = intTypeRef
		]
	}
	
	/**
	 * @return an operation with no arguments.
	 */
	def OperationSig nullOp() {
		rc.createOperationSig => [
			name = "op"
		]
	}

	private def intTypeRef() {
		rc.createTypeRef => [ref = intType]
	}

	private def intType() {
		rc.createPrimitiveType => [name = "int"]
	}

	private def sseq() {
		val s = seq
		rcert.createSubsequence => [
			s.body = it
		]
	}

	private def seq() {
		rcert.createSequence => [ x |
			x.group = group
			x.actors.add(mf.standardActor(TargetActorRelationship::WORLD))
			x.actors.add(mf.standardActor(TargetActorRelationship::TARGET))
		]
	}

	private def group() {
		rcert.createSequenceGroup => [ x |
			x.target = target()
		]
	}

	/**
	 * @return a mock target.
	 */
	def target() {
		rcert.createRCModuleTarget => [
			module = rcModule
		]
	}

	/**
	 * @return a mock RoboChart module.
	 */
	def rcModule() {
		rc.createRCModule => [name = "test"]
	}

	/**
	 * Checks that it appears to be the arrow factory's mock world.
	 * 
	 * @param it  the actor to check.
	 */
	def expectWorld(Actor it) {
		assertNotNull
		assertEquals(TargetActorRelationship::WORLD, calculatedRelationship)
	}

	/**
	 * Checks that it appears to be the arrow factory's mock target.
	 * 
	 * @param it  the actor to check.
	 */
	def expectTarget(Actor it) {
		assertNotNull
		assertEquals(TargetActorRelationship::TARGET, calculatedRelationship)
	}
}
