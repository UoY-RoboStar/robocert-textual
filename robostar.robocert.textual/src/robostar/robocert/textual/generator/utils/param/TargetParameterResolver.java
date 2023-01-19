/*
 * Copyright (c) 2021-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.utils.param;

import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import robostar.robocert.ConstAssignment;
import robostar.robocert.HasControllerTarget;
import robostar.robocert.HasModuleTarget;
import robostar.robocert.OperationTarget;
import robostar.robocert.StateMachineTarget;
import robostar.robocert.Target;
import robostar.robocert.util.resolve.ConstAssignmentResolver;

/**
 * Deduces the correct parameterisation for {@link Target}s, and handles filtering it for
 * instantiated constants.
 *
 * @author Matt Windsor
 */
public record TargetParameterResolver(ConstAssignmentResolver caResolver,
                                      RoboChartParameterResolver rcResolver,
                                      IQualifiedNameProvider qnp) {

  @Inject
  public TargetParameterResolver {
    Objects.requireNonNull(caResolver);
    Objects.requireNonNull(rcResolver);
    Objects.requireNonNull(qnp);
  }

  /**
   * Gets the full parameterisation for a target.
   *
   * <p>This contains every constant and (for operation targets) formal parameter that is visible
   * on the surface of the target.
   *
   * @param t the target for which we are trying to get the parameterisation
   * @return a stream of all parameters defined on this target's module
   */
  public Stream<Parameter> parameterisation(Target t) {
    if (t instanceof HasModuleTarget m) {
      return rcResolver.parameterisation(m.getModule());
    }
    if (t instanceof HasControllerTarget c) {
      return rcResolver.parameterisation(c.getController());
    }
    if (t instanceof StateMachineTarget s) {
      return rcResolver.parameterisation(s.getStateMachine());
    }
    if (t instanceof OperationTarget o) {
      return rcResolver.parameterisation(o.getOperation());
    }
    throw new IllegalArgumentException("don't know how to get parameterisation of %s".formatted(t));
  }

  /**
   * Filters the given stream to remove any parameters instantiated by the instantiation.
   *
   * <p>
   * This iterator should return a stable ordering of uninstantiated constants.
   *
   * @param s    the stream to filter
   * @param inst the instantiation in question (can be null)
   * @return an iterator of uninstantiated constant names
   * @apiNote If the instantiation is null, we return the stream unmodified.
   */
  public Stream<Parameter> excludeInstantiated(Stream<Parameter> s, List<ConstAssignment> inst) {
    if (inst == null) {
      return s;
    }

    final var keys = instantiatedKeys(inst);
    // other CharSequences might not have proper equality.
    return s.filter(x -> !keys.contains(x.qualifiedName(qnp)));
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

  private Set<QualifiedName> instantiatedKeys(List<ConstAssignment> inst) {
    return caResolver.constantsOf(inst).map(qnp::getFullyQualifiedName)
        .collect(Collectors.toUnmodifiableSet());
  }
}
