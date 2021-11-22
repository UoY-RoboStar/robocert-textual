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
import robocalc.robocert.model.robocert.RCModuleTarget;
import robocalc.robocert.model.robocert.SystemTarget;
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
	private RCModuleExtensions mx;
	@Inject
	private VariableExtensions vx;

	/**
	 * Gets the parameterisation for a target.
	 *
	 * @param t the target for which we are trying to get all constants.
	 *
	 * @return a stream of all constants defined on this target's module.
	 */
	public Stream<Variable> parameterisation(Target t) {
		if (t instanceof SystemTarget s)
			return mx.parameterisation(s.getEnclosedModule());
		if (t instanceof RCModuleTarget m)
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

		var keys = instantiatedKeys(inst);
		return s.filter(x -> !keys.contains(constantKey(x)));
	}

	private Set<String> instantiatedKeys(Instantiation inst) {
		return inst.getAssignments().stream().flatMap(x -> x.getConstants().stream()).map(this::constantKey)
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * @return a stringification of the given constant so as to be useful for
	 *         equality testing in the presence of multiple instances of constants
	 *         with the same name but possibly different contexts.
	 */
	private String constantKey(Variable it) {
		// We toString because CharSequences don't compare equal properly.
		return vx.constantId(it).toString();
	}
}
