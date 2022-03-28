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
import robocalc.robocert.model.robocert.ControllerTarget;
import robocalc.robocert.model.robocert.InControllerTarget;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTarget;
import robocalc.robocert.model.robocert.StateMachineTarget;
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
	 * Gets the full parameterisation for a target.
	 *
	 * <p>This contains every constant and (for operation targets) formal parameter that is visible
	 * on the surface of the target.
	 *
	 * @param t the target for which we are trying to get the parameterisation.
	 *
	 * @return a stream of all parameters defined on this target's module.
	 */
	public Stream<Parameter> parameterisation(Target t) {
		if (t instanceof InModuleTarget m)
			return rcResolver.parameterisation(m.getModule());
		if (t instanceof InControllerTarget c)
			return rcResolver.parameterisation(c.getController());
		if (t instanceof ModuleTarget m)
			return rcResolver.parameterisation(m.getModule());
		if (t instanceof ControllerTarget c)
			return rcResolver.parameterisation(c.getController());
		if (t instanceof StateMachineTarget s)
			return rcResolver.parameterisation(s.getStateMachine());
		if (t instanceof OperationTarget o)
			return rcResolver.parameterisation(o.getOperation());
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

	/**
	 * Filters from the stream any parameters with existing values at the RoboChart level.
	 *
	 * @param s the parameter stream to filter.
	 * @return the filtered stream.
	 */
	public Stream<Parameter> excludeWithValue(Stream<Parameter> s) {
		return s.filter(x -> x.tryGetConstant().map(k -> k.getInitial() == null).orElse(true));
	}

	private Set<String> instantiatedKeys(List<ConstAssignment> inst) {
		return instHelp.allConstants(inst).map(varHelp::constantId)
				.collect(Collectors.toUnmodifiableSet());
	}
}
