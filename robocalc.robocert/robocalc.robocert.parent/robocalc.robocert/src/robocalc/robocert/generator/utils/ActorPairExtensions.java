package robocalc.robocert.generator.utils;

import robocalc.robocert.model.robocert.ActorPair;
import robocalc.robocert.model.robocert.MessageDirection;
import robocalc.robocert.model.robocert.TargetActor;

/**
 * Extensions for {@link ActorPair}s.
 *
 * @author Matt Windsor
 */
public class ActorPairExtensions {
	/**
	 * Tries to infer the direction of this actor pair.
	 *
	 * An actor pair can only have a direction if exactly one of its actors is a
	 * target actor If so, then the direction is outbound if, and only if, that
	 * target actor is the from-actor.
	 *
	 * @param pair the actor pair to query.
	 * @return the direction of the actor pair.
	 * @throws UnsupportedOperationException if there is no single target.
	 */
	public MessageDirection getInferredDirection(ActorPair pair) {
		if (pair.getResolvedFrom() instanceof TargetActor)
			return MessageDirection.OUTBOUND;
		if (pair.getResolvedTo() instanceof TargetActor)
			return MessageDirection.INBOUND;
		throw new UnsupportedOperationException(
					"tried to infer direction of an actor pair with an ambiguous target");
	}
}
