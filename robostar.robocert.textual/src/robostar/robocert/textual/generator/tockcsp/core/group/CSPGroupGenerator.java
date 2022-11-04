/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.core.group;

import java.util.stream.Stream;
import robostar.robocert.CSPGroup;

/**
 * Generates CSP for CSP groups.
 *
 * @author Matt Windsor
 */
public class CSPGroupGenerator extends GroupGenerator<CSPGroup> {
	@Override
	protected Stream<CharSequence> generateBodyElements(CSPGroup group) {
		return Stream.of(group.getCsp());
	}

	@Override
	protected boolean isInModule(CSPGroup group) {
		// We don't put CSP groups in modules, as they are emitting CSP directly into the output.
		return false;
	}

	@Override
	protected CharSequence typeName(CSPGroup group) {
		return "CSP";
	}
}
