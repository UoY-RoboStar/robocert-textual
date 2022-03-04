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
package robocalc.robocert.generator.utils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import circus.robocalc.robochart.Variable;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.Target;

/**
 * Deduces the correct parameterisation for {@link Target}s, and handles
 * filtering it for instantiated constants.
 *
 * @author Matt Windsor
 */
public class TargetParameterResolver {
	// TODO(@MattWindsor91): move some of these to the metamodel?
	@Inject
	private RCModuleHelper mx;
	@Inject
	private VariableHelper vx;

	/**
	 * Gets the parameterisation for a target.
	 *
	 * @param t the target for which we are trying to get all constants.
	 *
	 * @return a stream of all constants defined on this target's module.
	 */
	public Stream<Variable> parameterisation(Target t) {
		if (t instanceof ModuleTarget m)
			return mx.parameterisation(m.getModule());
		throw new IllegalArgumentException("don't know how to get parameterisation of %s".formatted(t));
	}

	/**
	 * Filters the given stream to remove any parameters instantiated by the
	 * instantiation.
	 *
	 * This iterator should return a stable ordering of uninstantiated constants.
	 *
	 * @apiNote If the instantiation is null, we return the stream unmodified.
	 *
	 * @param s    the stream to filter.
	 * @param inst the instantiation in question (may be null).
	 * @return an iterator of uninstantiated constant names.
	 */
	public Stream<Variable> excludeInstantiated(Stream<Variable> s, Instantiation inst) {
		if (inst == null)
			return s;

		final var keys = instantiatedKeys(inst);
		// We rely on vx.constantId being a String here;
		// other CharSequences might not have proper equality.
		return s.filter(x -> !keys.contains(vx.constantId(x)));
	}

	private Set<String> instantiatedKeys(Instantiation inst) {
		return inst.getAssignments().stream().flatMap(x -> x.getConstants().stream()).map(vx::constantId)
				.collect(Collectors.toUnmodifiableSet());
	}
}
