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
public class GroupNamer extends OptionalNamer<Group> {
	@Inject
	private FilenameExtensions fe;

	/**
	 * The prefix appended to synthesised names, exposed for testing purposes.
	 */
	public static final String PREFIX = "Untitled_Group__";

	@Override
	protected String getPrefix() {
		return PREFIX;
	}

	@Override
	protected EList<Group> getContainer(Group it) {
		var pkg = it.getParent();
		return pkg == null ? null : pkg.getGroups();
	}

	@Override
	protected String getContainerName(Group it) {
		var pkg = it.getParent();
		return pkg == null ? null : fe.getFileBasename(pkg);
	}
}
