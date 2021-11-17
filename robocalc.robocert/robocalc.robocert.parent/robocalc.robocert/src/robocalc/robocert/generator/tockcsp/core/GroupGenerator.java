/**
 *
 */
package robocalc.robocert.generator.tockcsp.core;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.utils.name.GroupNamer;
import robocalc.robocert.model.robocert.Group;

/**
 * Abstract base for things that generate groups.
 *
 * @author Matt Windsor
 */
public abstract class GroupGenerator<T extends Group> {
	@Inject
	private GroupNamer gn;
	@Inject
	private CSPStructureGenerator csp;

	/**
	 * Generates CSP-M for a group.
	 *
	 * @param group the group in question.
	 *
	 * @return CSP-M for the group.
	 */
	public CharSequence generate(T group) {
		var body = join(generateBodyElements(group));
		var priv = join(generatePrivateElements(group));
		return String.join("\n", generateHeader(group), liftBody(group, body, priv), generateFooter(group));
	}

	//
	// Base functionality
	//

	/**
	 * Generates the body of the group.
	 *
	 * @param group the group in question.
	 *
	 * @return the CSP-M for the body of the group, as a stream of top-level
	 *         elements.
	 */
	protected abstract Stream<CharSequence> generateBodyElements(T group);

	/**
	 * Generates private elements for the group.
	 * 
	 * These are only used if isInModule is true, and are never timed.
	 * 
	 * @param group the group in question.
	 * 
	 * @return a stream of private elements for the group.
	 */
	protected Stream<CharSequence> generatePrivateElements(T group) {
		return Stream.empty();
	}
	
	/**
	 * Gets the name of the type of group, for use in headers and footers.
	 *
	 * @param group the group in question.
	 *
	 * @return the name of the type of group (eg 'UNTIMED CSP').
	 */
	protected abstract CharSequence typeName(T group);

	//
	// Configuration
	//

	/**
	 * Gets whether the group body should be placed in a timed section.
	 *
	 * @param group the group in question.
	 *
	 * @return true if the group should be lifted, false otherwise.
	 */
	protected abstract boolean isTimed(T group);

	/**
	 * Gets whether the group body should be placed in a module.
	 *
	 * The module is given the same name as the group.
	 *
	 * @param group the group in question.
	 *
	 * @return true if the group should be lifted, false otherwise.
	 */
	protected abstract boolean isInModule(T group);

	//
	// Implementation details
	//

	private CharSequence liftBody(T group, CharSequence body, CharSequence privBody) {
		body = isTimed(group) ? csp.timed(body) : body;
		if (!isInModule(group))
			return body;
		
		var name = gn.getOrSynthesiseName(group);
		return privBody.isEmpty() ? csp.module(name, body) : csp.moduleWithPrivate(name, privBody, body);
	}

	private CharSequence join(Stream<CharSequence> elements) {
		return elements.collect(Collectors.joining("\n\n"));
	}

	private CharSequence generateHeader(T group) {
		return "--- BEGIN %s GROUP %s".formatted(typeName(group), gn.getOrSynthesiseName(group));
	}

	private CharSequence generateFooter(T group) {
		return "--- END %s GROUP".formatted(typeName(group));
	}
}
