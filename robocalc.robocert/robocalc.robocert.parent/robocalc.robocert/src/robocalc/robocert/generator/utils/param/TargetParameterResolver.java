/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/
package robocalc.robocert.generator.utils.param;

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import robocalc.robocert.generator.utils.VariableHelper;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.util.InstantiationHelper;

/**
 * Deduces the correct parameterisation for {@link Target}s, and handles
 * filtering it for instantiated constants.
 *
 * @author Matt Windsor
 */
public record TargetParameterResolver(InstantiationHelper instHelp, RoboChartParameterResolver rcResolver, VariableHelper varHelp) {
	// TODO(@MattWindsor91): if we manage to get rid of varHelp here, we might
	// be able to move this to the model helpers.
	
	@Inject
	public TargetParameterResolver {
		Objects.requireNonNull(instHelp);
		Objects.requireNonNull(rcResolver);
		Objects.requireNonNull(varHelp);
	}

	/**
	 * Gets the parameterisation for a target.
	 *
	 * @param t the target for which we are trying to get all constants.
	 *
	 * @return a stream of all parameters defined on this target's module.
	 */
	public Stream<Parameter> parameterisation(Target t) {
		if (t instanceof ModuleTarget m)
			return rcResolver.parameterisation(m.getModule());
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
	 * @param gu   used for working out names of constants.
	 * @param s    the stream to filter.
	 * @param inst the instantiation in question (may be null).
	 * @return an iterator of uninstantiated constant names.
	 */
	public Stream<Parameter> excludeInstantiated(CTimedGeneratorUtils gu, Stream<Parameter> s, List<ConstAssignment> inst) {
		// TODO(@MattWindsor91): the use of CSP IDs/gu here to distinguish different parameters is a
		// bit awkward.
		if (inst == null)
			return s;

		final var keys = instantiatedKeys(inst);
		// We rely on constantId being a String here;
		// other CharSequences might not have proper equality.
		return s.filter(x -> !keys.contains(x.cspId(gu)));
	}

	private Set<String> instantiatedKeys(List<ConstAssignment> inst) {
		return instHelp.allConstants(inst).map(varHelp::constantId)
				.collect(Collectors.toUnmodifiableSet());
	}
}
