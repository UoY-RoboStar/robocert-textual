/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.utils.param.TargetParameterResolver;
import robocalc.robocert.model.robocert.ComponentTarget;
import robocalc.robocert.model.robocert.ControllerTarget;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTarget;
import robocalc.robocert.model.robocert.StateMachineTarget;
import robocalc.robocert.model.robocert.util.StreamHelper;
import robocalc.robocert.model.robocert.util.resolve.ControllerResolver;
import robocalc.robocert.model.robocert.util.resolve.ModuleResolver;
import robocalc.robocert.model.robocert.util.resolve.StateMachineResolver;

/**
 * Handles generation of bodies of component targets (ModuleTarget, ControllerTarget, etc).
 *
 * @param ctrlRes  resolves aspects of controllers, such as names.
 * @param gu       RoboChart generator utilities.
 * @param csp      low-level CSP generator.
 * @param paramRes resolves target parameterisations.
 * @param termGen  generates CSP to hide termination channels.
 * @author Matt Windsor
 */
public record ComponentTargetBodyGenerator(ModuleResolver modRes,
                                           ControllerResolver ctrlRes, StateMachineResolver stmRes,
                                           CSPStructureGenerator csp,
                                           CTimedGeneratorUtils gu,
                                           TargetParameterResolver paramRes,
                                           TerminationGenerator termGen) {

  /**
   * Constructs a component generator.
   *
   * @param modRes  resolves aspects of modules, such as names.
   * @param ctrlRes  resolves aspects of controllers, such as names.
   * @param stmRes  resolves aspects of state machine bodies, such as names.
   * @param gu       RoboChart generator utilities.
   * @param csp      low-level CSP generator.
   * @param paramRes resolves target parameterisations.
   * @param termGen  generates CSP to hide termination channels.
   */
  @Inject
  public ComponentTargetBodyGenerator {
    Objects.requireNonNull(modRes);
    Objects.requireNonNull(ctrlRes);
    Objects.requireNonNull(stmRes);
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
    Objects.requireNonNull(paramRes);
    Objects.requireNonNull(termGen);
  }

  /**
   * Eventually this should be exposed to the user.
   */
  private static final boolean USE_OPTIMISED_TARGETS = true;

  /**
   * Generates CSP for the body of a component target definition.
   *
   * @param t the target in question.
   * @return CSP-M for the target body (usually a reference to the RoboChart semantics, potentially
   * with termination hiding).
   */
  public CharSequence generate(ComponentTarget t) {
    var body = generateCall(t);

    // Module targets hide their termination channel, but other targets do not.
    // We need to hide it ourselves in those cases.
    if (!(t instanceof ModuleTarget)) {
      final var ns = csp.namespaced(namespace(t));

      body = termGen.hideTerminate(ns, body);

      // Operation targets also have an extra share-CSP channel that needs to be hidden.
      if (t instanceof OperationTarget) {
        body = csp.bins().hide(body, csp.sets().set(csp.namespaced(ns, "share__")));
      }
    }

    return body;
  }

  private CharSequence generateCall(ComponentTarget t) {
    // We now assume that we have a component target; these are just references to the RoboChart
    // semantics-generated processes.

    final var params = paramRes.parameterisation(t).toList();
    /*
     * In email with Pedro (2021-08-04): the target of a refinement against a (simple)
     * specification should usually be unoptimised (D__); model comparisons should
     * usually be optimised (O__).  However, in practice c. 2022-03-28, it seems that O__ is
     * outperforming D__ across the board and is safe to enable.  This might change gain in future.
     *
     * TODO(@MattWindsor91): eventually, we should be able to select the
     * optimisation level.
     */
    final var name = gu.getFullProcessName(t.getElement(), false, USE_OPTIMISED_TARGETS);
    final var args = StreamHelper.push(TargetGenerator.ID, params.stream().map(k -> k.cspId(gu)))
        .toArray(CharSequence[]::new);

    return csp.function(name, args);
  }

  private String[] namespace(ComponentTarget t) {
    // TODO(@MattWindsor91): this logic is repeated in several other places, I think.
    if (t instanceof ModuleTarget m) {
      return modRes.name(m.getModule());
    }
    if (t instanceof ControllerTarget c) {
      return ctrlRes.name(c.getController());
    }
    if (t instanceof StateMachineTarget b) {
      return stmRes.name(b.getStateMachine());
    }
    if (t instanceof OperationTarget o) {
      return stmRes.name(o.getOperation());
    }
    throw new IllegalArgumentException("can't get namespace of target: %s".formatted(t));
  }
}
