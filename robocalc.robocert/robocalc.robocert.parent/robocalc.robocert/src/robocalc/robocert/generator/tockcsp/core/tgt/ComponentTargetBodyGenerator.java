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
import robocalc.robocert.model.robocert.util.StreamHelper;
import robocalc.robocert.model.robocert.util.resolve.ControllerResolver;

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
public record ComponentTargetBodyGenerator(ControllerResolver ctrlRes, CSPStructureGenerator csp,
                                           CTimedGeneratorUtils gu,
                                           TargetParameterResolver paramRes,
                                           TerminationGenerator termGen) {

  /**
   * Constructs a component generator.
   *
   * @param ctrlRes  resolves aspects of controllers, such as names.
   * @param gu       RoboChart generator utilities.
   * @param csp      low-level CSP generator.
   * @param paramRes resolves target parameterisations.
   * @param termGen  generates CSP to hide termination channels.
   */
  @Inject
  public ComponentTargetBodyGenerator {
    Objects.requireNonNull(ctrlRes);
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

    if (t instanceof ControllerTarget c) {
      // Component targets are mostly just references to the RoboChart semantics-generated processes,
      // with one caveat: we have to hide the termination channel for controllers.
      final var name = csp.namespaced(ctrlRes.name(c.getController()));
      body = termGen.hideTerminate(name, body);
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
}
