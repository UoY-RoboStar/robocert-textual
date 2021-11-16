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
package robocalc.robocert.generator.tockcsp.ll;

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.GroupGenerator;
import robocalc.robocert.model.robocert.CSPGroup;

/**
 * Generator for CSP fragment groups.
 *
 * @author Matt Windsor
 */
public class CSPGroupGenerator extends GroupGenerator<CSPGroup> {
	// TODO(@MattWindsor91): part-merge with sequence group generation?
	// TODO(@MattWindsor91): allow modularisation?
	@Inject
	private CSPFragmentGenerator cg;

	@Override
	protected Stream<CharSequence> generateBodyElements(CSPGroup group) {
		return group.getFragments().stream().map(cg::generate);
	}

	@Override
	protected boolean isTimed(CSPGroup group) {
		// This double negative is not nice, but is foisted upon us by the
		// combination of metamodel and parser.
		return !group.isUntimed();
	}

	@Override
	protected boolean isInModule(CSPGroup group) {
		// TODO(@MattWindsor91): change this to case-by-case or true
		return false;
	}

	@Override
	protected CharSequence typeName(CSPGroup group) {
		return (group.isUntimed() ? "UNTIMED " : "") + "CSP";
	}
}
