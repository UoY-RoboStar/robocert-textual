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
package robocalc.robocert.generator.utils.name;

import org.eclipse.emf.common.util.EList;

import com.google.common.base.Strings;

import robocalc.robocert.model.robocert.OptionallyNamedElement;

/**
 * Implements a fallback naming scheme for providing names to things that may
 * not have one.
 *
 * @author Matt Windsor
 *
 * @param <T> Type being named.
 */
public abstract class OptionalNamer<T extends OptionallyNamedElement> {
	/**
	 * Gets or synthesises a name for the given element.
	 *
	 * @implNote If the element has a name, we generally return that name.
	 *           Otherwise, we deterministically synthesise a new name that should
	 *           be unique to the element inside its parent scope.
	 *
	 * @apiNote If one thing in the generator uses this name, everything should, to
	 *          avoid broken references.
	 *
	 * @param it the element for which we need a name.
	 *
	 * @return the retrieved or synthesised name of the group.
	 */
	public String getOrSynthesiseName(T it) {
		var name = it.getName();
		return Strings.isNullOrEmpty(name) ? synthesiseName(it) : avoidClashes(name);
	}

	/**
	 * Generates a name for an element that doesn't have one.
	 *
	 * @param it the element for which we are generating a name.
	 *
	 * @return a systematic name related to the element's location in a parent
	 *         scope.
	 */
	private String synthesiseName(T it) {
		var prefix = getPrefix();

		// NOTE(@MattWindsor91):
		// The idea here is to use the physical location of the group within
		// its package, as well as the name of the package itself, as the
		// name. It isn't necessarily foolproof (and should be improved if
		// it isn't), but should hold fairly well.
		var container = getContainer(it);
		if (container == null)
			return prefix;

		var index = container.indexOf(it);
		return "%s%d_%s".formatted(prefix, index, getContainerName(it));
	}

	/**
	 * Tries to avoid clashes between the group naming system and any custom names
	 * written by the user.
	 *
	 * Such clashes should be rare, but might happen.
	 *
	 * @param name the name supplied for the group.
	 *
	 * @return a hopefully clash-avoided name.
	 */
	private String avoidClashes(String name) {
		var prefix = getPrefix();
		return name.startsWith(prefix) ? prefix + "A_" + name.substring(prefix.length()) : name;
	}

	/**
	 * Gets the prefix that will be added to synthesised names.
	 *
	 * @return the prefix.
	 */
	protected abstract String getPrefix();

	/**
	 * Gets the list of objects in which the object-to-name can be found.
	 *
	 * @apiNote The given object must be in this list, if it is non-null.
	 *
	 * @param it the object for which we want the container.
	 *
	 * @return the object's containing list (may be null).
	 */
	protected abstract EList<T> getContainer(T it);

	/**
	 * Gets a name for the containing scope of the object.
	 *
	 * @param it the object for which we want the container name.
	 *
	 * @return the object's container's name.
	 */
	protected abstract String getContainerName(T it);
}
