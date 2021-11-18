/**
 * 
 */
package robocalc.robocert.model.robocert.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.inject.Inject;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.Edge;
import robocalc.robocert.model.robocert.Argument;
import robocalc.robocert.model.robocert.ImplicitEdge;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.RoboCertFactory;

/**
 * High-level factory for message-related objects.
 * 
 * @author Matt Windsor
 */
public class MessageFactory {
	@Inject private RoboCertFactory rc;

	/**
	 * Constructs a message spec with the given topic, edge, and arguments.
	 * 
	 * @param topic  the topic to use for the message spec.
	 * @param edge the edge to use for the message spec.
	 * @param args   the arguments to use for the message spec.
	 * 
	 * @return the specification.
	 */
	public MessageSpec spec(MessageTopic topic, Edge edge, Argument ...args) {
		return spec(topic, edge, Arrays.asList(args));
	}
	
	/**
	 * Constructs a message spec with the given topic, edge, and argument collection.
	 * 
	 * @param topic  the topic to use for the message spec.
	 * @param edge the edge to use for the message spec.
	 * @param args   the arguments to use for the message spec.
	 * 
	 * @return the specification.
	 */
	public MessageSpec spec(MessageTopic topic, Edge edge, Collection<? extends Argument> args) {
		var it = rc.createMessageSpec();
		it.setTopic(topic);
		it.setEdge(edge);
		it.getArguments().addAll(args);
		return it;
	}
	
	/**
	 * Constructs an event topic with the given event.
	 * 
	 * @param e the event to use.
	 *
	 * @return the event topic.
	 */
	public MessageTopic eventTopic(Event e) {
		var it = rc.createEventTopic();
		it.setEvent(e);
		return it;
	}
	
	/**
	 * Constructs an operation topic with the given operation.
	 * 
	 * @param o the signature of the operation to use.
	 *
	 * @return the event topic.
	 */
	public MessageTopic opTopic(OperationSig o) {
		var it = rc.createOperationTopic();
		it.setOperation(o);
		return it;
	}
	
	/**
	 * Constructs a directional edge.
	 * 
	 * @param d the direction to construct a pair over.
	 * 
	 * @return the edge.
	 */
	public ImplicitEdge directional(EdgeDirection d) {
		var it = rc.createImplicitEdge();
		it.setDirection(d);
		return it;
	}
	
	/**
	 * @return a list containing target and world actors.
	 */
	public List<Actor> targetWorldActors() {
		return List.of(rc.createTargetActor(), rc.createWorldActor());
	}
}
