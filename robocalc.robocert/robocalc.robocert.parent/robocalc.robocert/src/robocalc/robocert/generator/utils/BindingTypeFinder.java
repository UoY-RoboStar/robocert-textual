package robocalc.robocert.generator.utils;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;

import circus.robocalc.robochart.Type;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.MessageSpec;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.WildcardArgument;

/**
 * Finds the RoboChart type of bindings by traversing the object graph back
 * to where they were used.
 * 
 * @author Matt Windsor
 */
public class BindingTypeFinder {
	// TODO(@MattWindsor91): expose this through the metamodel.
	
	@Inject private TopicExtensions tx;
	
	/**
	 * Gets the type of a {@link Binding} by traversing its container(s).
	 * 
	 * For instance, a binding within a {@link MessageSpec} will be resolved
	 * against the parameters of its corresponding {@link MessageTopic}.
	 * 
	 * @param b  the binding whose type is to be resolved.
	 * @return  the RoboChart type of the binding.
	 */
	public Type getType(Binding b) {
		var parent = b.eContainer();

		if (parent instanceof WildcardArgument arg) {
			return typeFromWildcardArgument(arg);
		}
		
		// Add other locations for bindings here.
		
		throw new UnsupportedOperationException("Unsupported binding container: " + parent);
	}
	
	private Type typeFromWildcardArgument(WildcardArgument arg) {
		var parent = arg.eContainer();
		
		if (parent instanceof MessageSpec spec) {
			return typeFromMessageSpec(spec, arg);
		}
		
		// If WildcardArguments can ever come from things other than message
		// specs, add code for them here.
		
		throw new UnsupportedOperationException("Unsupported wildcard argument container: " + parent);
	}

	private Type typeFromMessageSpec(MessageSpec spec, WildcardArgument arg) {
		var index = spec.getArguments().indexOf(arg);
		if (index == -1) {
			throw new IndexOutOfBoundsException("couldn't find index of argument: " + arg);
		}
		return Iterators.get(tx.paramTypes(spec.getTopic()), index);
	}
}
