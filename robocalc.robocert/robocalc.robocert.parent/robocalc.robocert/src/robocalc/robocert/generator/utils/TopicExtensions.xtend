package robocalc.robocert.generator.utils

import robocalc.robocert.model.robocert.EventTopic
import java.util.Collections
import robocalc.robocert.model.robocert.OperationTopic
import circus.robocalc.robochart.RoboChartFactory
import com.google.inject.Inject
import circus.robocalc.robochart.Parameter
import java.util.Iterator
import static extension com.google.common.collect.Iterators.*
import circus.robocalc.robochart.Type
import org.eclipse.xtext.EcoreUtil2

/**
 * Extensions for topics.
 */
class TopicExtensions {
	// TODO(@MattWindsor91): expose some of this through the metamodel.
	/**
	 * Used for producing pseudo-parameters for events.
	 */
	@Inject RoboChartFactory rc

	/**
	 * Iterates over all of the parameter types in an event.
	 * 
	 * An event has at most one type, depending on if it is typed or not.
	 * 
	 * @param it  the event topic for which we are getting parameter types.
	 * 
	 * @return the parameter types of the event.
	 */
	def dispatch Iterator<Type> paramTypes(EventTopic it) {
		event.type?.singletonIterator ?: Collections.emptyIterator
	}

	/**
	 * Iterates over all of the parameters types in an operation.
	 * 
	 * @param it  the operation topic for which we are getting parameter type.
	 * 
	 * @return the parameter types of the operation.
	 */
	def dispatch Iterator<Type> paramTypes(OperationTopic it) {
		operation.parameters.iterator.map[type]
	}

	/**
	 * Iterates over all of the parameters in an event.
	 * 
	 * An event has at most one parameter, depending on if it is typed or not.
	 * The parameter, strictly speaking, doesn't actually exist at the
	 * RoboChart level; we just treat the event type as one for convenience.
	 * 
	 * @param it  the event topic for which we are getting parameters.
	 * 
	 * @return the parameters of the event.
	 */
	def dispatch Iterator<Parameter> params(EventTopic it) {
		event.type?.eventTypeToParam?.singletonIterator ?: Collections.emptyIterator
	}

	/**
	 * Iterates over all of the parameters in an operation.
	 * 
	 * @param it  the operation topic for which we are getting parameters.
	 * 
	 * @return the parameters of the operation.
	 */
	def dispatch Iterator<Parameter> params(OperationTopic it) {
		operation.parameters.iterator
	}

	/**
	 * Wraps an event type into a parameter, so that the rest of the
	 * generator can treat it as one.
	 * 
	 * @param it  the type to expand.
	 * 
	 * @return the type as a parameter.
	 */
	private def Parameter eventTypeToParam(Type t) {
		rc.createParameter => [
			name = EVENT_PARAM_NAME
			/* We can't just say 'type = t' here: it introduces interesting
			 * aliasing issues that cause the original type to be nullified at
			 * strange times.
			 */
			type = EcoreUtil2.copy(t)
		]
	}

	/**
	 * The arbitrary name assigned to event-types-as-parameters.
	 */
	static final String EVENT_PARAM_NAME = "x"
}
