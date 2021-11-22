/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.validation;

import java.util.function.Function;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ComposedChecks;

import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.RoboCertPackage;
import robocalc.robocert.model.robocert.SequenceGroup;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.WorldActor;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@ComposedChecks(validators={ActorValidator.class})
public class RoboCertValidator extends AbstractRoboCertValidator {
	//
	// MessageSpec
	//

	public static final String EDGE_ACTORS_INDISTINCT = "edgeActorsIndistinct";
	public static final String OPERATION_NEEDS_TARGET = "operationNeedsTarget";
	public static final String OPERATION_FROM_WORLD = "operationFromWorld";

	/**
	 * Checks that an edge is valid.
	 *
	 * @param s the spec to check.
	 */
	@Check
	public void checkEdgeFlow(MessageSpec s) {
		var e = s.getEdge();
		var from = e.getResolvedFrom();
		var to = e.getResolvedTo();
		if (EcoreUtil.equals(from, to))
			edgeError("A message cannot mention the same actor at both endpoints", EDGE_ACTORS_INDISTINCT);
	}

	/**
	 * Checks that the flow of an operation message is valid.
	 *
	 * @param s the spec to check.
	 */
	@Check
	public void checkMessageSpecOperationFlow(MessageSpec s) {
		// This check is only relevant for operation topics.
		if (!(s.getTopic() instanceof OperationTopic))
			return;

		var edge = s.getEdge();

		var from = edge.getResolvedFrom();
		var fromIsTarget = from instanceof TargetActor;
		var fromIsWorld = from instanceof WorldActor;

		var to = edge.getResolvedTo();
		var toIsTarget = to instanceof TargetActor;

		// TODO(@MattWindsor91): make sure we properly handle other kinds of
		// sequence.

		if (!fromIsTarget && !toIsTarget)
			edgeError("At least one of the endpoints of an operation must be the target", OPERATION_NEEDS_TARGET);

		if (fromIsWorld)
			edgeError("An operation message cannot originate from the world", OPERATION_FROM_WORLD);
	}

	private void edgeError(String string, String code) {
		error(string, RoboCertPackage.Literals.MESSAGE_SPEC__EDGE, code);
	}

	//
	// SequenceGroup
	//

	public static final String TOO_MANY_TARGETS = "tooManyTargets";
	public static final String TOO_MANY_WORLDS = "tooManyWorlds";
	public static final String TARGET_NEEDS_WORLD = "targetNeedsWorld";
	public static final String WORLD_NEEDS_TARGET = "worldNeedsTarget";

	/**
	 * Checks that the actors of a target-and-world sequence are valid.
	 *
	 * @param s the sequence to check.
	 */
	@Check
	public void checkSequenceGroupActorsTargetWorld(SequenceGroup g) {
		var targets = numTargets(g);
		var worlds = numWorlds(g);

		// Check only relevant when we have at least one of each of the above
		if (0 == targets + worlds)
			return;

		if (1 < targets)
			actorError("At most one actor in a sequence group can be the target", TOO_MANY_TARGETS);

		if (1 < worlds)
			actorError("At most one actor in a sequence group can be the world", TOO_MANY_WORLDS);

		// TODO(@MattWindsor91): these will need relaxing as we move to
		// non-system sequences.
		
		if (0 == worlds)
			actorError("A sequence group with one target actor requires a world actor", TARGET_NEEDS_WORLD);

		if (0 == targets)
			actorError("A sequence group with one world actor requires a target actor", WORLD_NEEDS_TARGET);
	}

	private void actorError(String string, String code) {
		error(string, RoboCertPackage.Literals.SEQUENCE_GROUP__ACTORS, code);
	}

	//
	// Utility functions
	//

	private long numTargets(SequenceGroup g) {
		return countActors(g, x -> x instanceof TargetActor);
	}

	private long numWorlds(SequenceGroup g) {
		return countActors(g, x -> x instanceof WorldActor);
	}

	private long countActors(SequenceGroup g, Function<? super Actor, Boolean> f) {
		return g.getActors().stream().mapToLong(x -> f.apply(x) ? 1 : 0).sum();
	}
}
