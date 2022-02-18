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
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.core;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Target;

/**
 * Generates CSP-M for {@link Target}s.
 *
 * @author Matt Windsor
 */
public class TargetGenerator {
  // TODO(@MattWindsor91): split reference resolution out of this.

  /** Module inside the translation of a CertPackage containing the (uninstantiated) target. */
  private static final String TARGET_MODULE = "Target";

  @Inject private CSPStructureGenerator csp;
  @Inject private TargetBodyGenerator tg;

  /**
   * Generates CSP-M for a {@link Target}.
   *
   * @param t the target for which we are generating a module.
   * @return a target module definition for this target list
   */
  public CharSequence generate(Target t) {
    return csp.module(TARGET_MODULE, moduleBody(t));
  }

  private CharSequence moduleBody(Target t) {
    // TODO(@MattWindsor91): the open/closed def could do with being
    // deduplicated with the work in SpecGroupGenerator.
    return String.join("\n", tickTockContext(t), universe(t), openDef(t), closedDef(t));
  }

  private CharSequence tickTockContext(Target t) {
    return csp.instance(
        TargetField.TICK_TOCK_CONTEXT.toString(), csp.function("model_shifting", semEvents(t)));
  }

  private CharSequence universe(Target t) {
    // TODO(@MattWindsor91): GitHub #76: this allows events in more
    // directions than should necessarily be allowed.
    return csp.definition(TargetField.UNIVERSE.toString(), semEvents(t));
  }

  /**
   * Produces an 'open' reference to the target's definition.
   *
   * <p>This is used by specification groups.
   *
   * @param t the target in question.
   * @return CSP-M referencing the name of the RoboStar module that defines the target.
   */
  private CharSequence openDef(Target t) {
    // TODO(@MattWindsor91): the argument here should be configurable.
    return csp.definition(TargetField.OPEN.toString(), tg.generateDef(t));
  }

  /**
   * Produces a closed definition for the target.
   *
   * <p>This is used by things wanting a CSP process.
   *
   * @param t the target in question.
   * @return CSP-M referencing the name of the RoboStar module that defines the target.
   */
  private CharSequence closedDef(Target t) {
    // TODO(@MattWindsor91): the argument here should be configurable.
    // TODO(@MattWindsor91): should these targets have an initial instantiation?
    final var def =
        csp.function(TargetField.OPEN.toString(), tg.generateRefParams(t, null, null, true));
    return csp.definition(TargetField.CLOSED.toString(), def);
  }

  //
  // References
  //

  private CharSequence semEvents(Target t) {
    return csp.namespaced(t.getElement().getName(), "sem__events");
  }

  /**
   * Gets the full namespaced name of the given field of a target.
   *
   * @param f the field to reference.
   * @return the name of the field from the perspective of code outside the target group.
   */
  public CharSequence getFullCSPName(TargetField f) {
    return csp.namespaced(TARGET_MODULE, f.toString());
  }
}
