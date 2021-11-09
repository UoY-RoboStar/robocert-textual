package robocalc.robocert.generator.utils;

import org.eclipse.emf.ecore.util.EcoreUtil;

import robocalc.robocert.model.robocert.ActorPair;
import robocalc.robocert.model.robocert.MessageDirection;
import robocalc.robocert.model.robocert.Target;

/**
 * Extensions for {@link ActorPair}s.
 *
 * @author Matt Windsor
 */
public class ActorPairExtensions {
	/**
	 * Gets the single {@link Target} of an actor pair, if one indeed exists.
	 *
	 * This is a temporary extension; our intention is to have it be removed when we
	 * expand the semantics to handle multiple targets, in which case there will no
	 * longer be a single target to get.
	 *
	 * @param pair the actor pair to query.
	 * @return the target, or null if none exists.
	 */
	public Target getTarget(ActorPair pair) {
		if (pair.getResolvedFrom()instanceof Target f)
			return f;
		if (pair.getResolvedTo()instanceof Target t)
			return t;
		return null;
	}

	/**
	 * Tries to infer the direction of this actor pair.
	 *
	 * An actor pair can only have a direction if exactly one of its actors is a
	 * {@link Target}. If so, then the direction is outbound if, and only if, that
	 * {@link Target} is the from-actor.
	 *
	 * @param pair the actor pair to query.
	 * @return the direction of the actor pair.
	 * @throws UnsupportedOperationException if there is no single target.
	 */
	public MessageDirection getInferredDirection(ActorPair pair) {
		var target = getTarget(pair);
		if (target == null)
			throw new UnsupportedOperationException(
					"tried to infer direction of an actor pair without an unambiguous target");
		return EcoreUtil.equals(target, pair.getResolvedFrom()) ? MessageDirection.OUTBOUND : MessageDirection.INBOUND;
	}
}
