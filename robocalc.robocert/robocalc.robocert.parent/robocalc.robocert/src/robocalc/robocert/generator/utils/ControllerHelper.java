/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alvaro Miyazawa, Pedro Ribeiro - initial definition (RoboChart CSP generator)
 *   Matt Windsor - porting to RoboCert
 ********************************************************************************/
package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;

/**
 * Helper for dealing with RoboChart controllers.
 *
 * @author Matt Windsor
 */
public record ControllerHelper(CTimedGeneratorUtils gu, DefinitionResolver dr) {
	@Inject
	public ControllerHelper {
		Objects.requireNonNull(gu);
		Objects.requireNonNull(dr);
	}

	/**
	 * Gets this controller's contribution to its module's parameterisation.
	 * 
	 * @param it  the controller.
	 * 
	 * @return the stream of variables that should be added to the module
	 *         parameterisation to account for this controller.
	 */
	public Stream<Variable> moduleParameterisation(ControllerDef it) {
		// TODO(@MattWindsor91): is this the controller parameterisation too?
		return Stream.concat(
			constantsOf(it),
			Stream.concat(
					it.getMachines().stream().map(dr::resolve).flatMap(this::constantsOf),
					it.getLOperations().stream().map(dr::resolve).flatMap(this::constantsOf)
			)
		);
	}

	private Stream<Variable> constantsOf(EObject it) {
		return gu.allLocalConstants(it).stream();
	}
}
