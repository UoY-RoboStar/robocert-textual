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

import com.google.common.base.Strings;
import com.google.inject.Inject;

import robocalc.robocert.generator.utils.FilenameExtensions;
import robocalc.robocert.model.robocert.Group;

/**
 * Determines a hopefully-unambigous name for {@link Group}s.
 *
 * This class is needed because we often need to generate CSP-M modules for
 * groups, but not all groups have names.
 *
 * @author Matt Windsor
 */
public class GroupNamer {
	@Inject
	private FilenameExtensions fe;

	/**
	 * Gets or synthesises a name for the given group.
	 *
	 * If the group has a name, we generally return that name. Otherwise, we
	 * deterministically synthesise a new name that should be unique to the group
	 * inside the resource set.
	 *
	 * If one thing in the generator uses this name, everything should, to avoid
	 * broken references.
	 *
	 * @param g the group for which we need a name.
	 *
	 * @return the retrieved or synthesised name of the group.
	 */
	public String getOrSynthesiseName(Group g) {
		var name = g.getName();
		return Strings.isNullOrEmpty(name) ? synthesiseName(g) : avoidClashes(name);
	}

	/**
	 * Generates a name for a group that doesn't have one.
	 *
	 * @param g the group for which we are generating a name.
	 *
	 * @return a systematic name related to the group's location in the package.
	 */
	private String synthesiseName(Group g) {
		// NOTE(@MattWindsor91):
		// The idea here is to use the physical location of the group within
		// its package, as well as the name of the package itself, as the
		// name. It isn't necessarily foolproof (and should be improved if
		// it isn't), but should hold fairly well.
		var pkg = g.getParent();
		if (pkg == null)
			return PREFIX;

		var index = pkg.getGroups().indexOf(g);
		return "%s%d_%s".formatted(PREFIX, index, fe.getFileBasename(pkg));
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
		return name.startsWith(PREFIX) ? PREFIX + "A_" + name.substring(PREFIX.length()) : name;
	}

	/**
	 * The prefix appended to synthesised names, exposed for testing purposes.
	 */
	public static final String PREFIX = "Untitled_Group__";
}
