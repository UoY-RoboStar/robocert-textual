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
 *   Pedro Ribeiro - initial definition (circus.robocalc.robochart.generator.csp)
 *   Matt Windsor - porting to RoboCert
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.core;

import com.google.inject.Inject;
import java.util.Objects;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.core.group.SpecificationGroupElementFinder;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.CoreProperty;
import robocalc.robocert.model.robocert.CorePropertyType;

/**
 * Generates unary 'core assertions': high level assertions such as divergence and deadlock
 * freedom.
 *
 * @author Matt Windsor
 */
public record CorePropertyGenerator(SpecificationGroupElementFinder elementFinder,
                                    CSPStructureGenerator csp) {

  @Inject
  public CorePropertyGenerator {
    Objects.requireNonNull(elementFinder);
    Objects.requireNonNull(csp);
  }

  /**
   * Generates CSP-M for a core property.
   *
   * @param p the property in question.
   * @return generated CSP-M for a core property.
   */
  public CharSequence generate(CoreProperty p) {
    Objects.requireNonNull(p, "can't generate for a null core property");
    Objects.requireNonNull(p.getGroup(), "core property must reference a group");

    final var t = p.getType();
    final var neg = p.isNegated();

    // Most properties have a straightforward encoding into tock-CSP,
    // as they translate directly into CSP-M assertions with negation
    // handled with 'assert/assert not'. Any exceptions go here:

    // All of the below are properties on the target itself.
    // If we add clock reachability (for instance), it isn't a property on
    // the process, and it'll go before this bit.
    // Similar situation for properties that require the tick-tock model.

    // TODO(@MattWindsor91): assertion?
    // TODO(@MattWindsor91): what about instantiations?
    final var proc = elementFinder.getFullCSPName(p.getGroup(), SpecGroupParametricField.TARGET);

    if (t == CorePropertyType.TERMINATION) {
      return neg ? generateNontermination(proc) : generateTermination(proc);
    }

    // All other properties can be handled like this:
    return csp.assertion(neg, generateSimple(proc, t));
  }

  //
  // Termination
  //

  private CharSequence generateNontermination(CharSequence proc) {
    return "-- nontermination\n" + csp.assertion(false,
        csp.refine("STOP", "%s\\Events;r__ -> SKIP".formatted(proc), "T"));
  }

  private CharSequence generateTermination(CharSequence proc) {
    // A termination check has two components:
    // 1) the process must deadlock and timelock;
    final var deadlocks = csp.assertion(true, generateSimple(proc, CorePropertyType.DEADLOCK_FREE));
    // 2) adding a perpetually-live event must remove the deadlock, eg the
    // deadlock was a SKIP.
    final var rproc = "%s; RUN({r__})".formatted(proc);
    final var rescuable = csp.assertion(false,
        generateSimple(rproc, CorePropertyType.DEADLOCK_FREE));
    return String.join("\n", "-- termination", deadlocks, rescuable);
  }

  //
  // Single-assertion properties
  //

  private CharSequence generateSimple(CharSequence proc, CorePropertyType t) {
    // As above, handle the special cases first:

    if (t == CorePropertyType.TIMELOCK_FREE) {
      return generateTimelockFreedom(proc);
    }
    // This is slightly more complex than just using FDR's deadlock freedom
    // check -- see later on.
    if (t == CorePropertyType.DEADLOCK_FREE) {
      return generateTimedDeadlockFreedom(proc);
    }

    // Everything else remaining is just a FDR builtin.
    return csp.tauPrioritiseTock("%s :[%s]".formatted(proc, generateFDRBuiltin(t)));
  }

  /**
   * Generates a timelock freedom assertion body.
   *
   * @param proc CSP-M for the process to check for timelock freedom.
   * @return the body of the timelock freedom assertion (eg, missing 'assert' or 'assert not').
   */
  private CharSequence generateTimelockFreedom(CharSequence proc) {
    return csp.tauPrioritiseTock(
        csp.refine("RUN({tock}) ||| CHAOS(diff(Events, {|tock|}))", proc, "F"));
  }

  /**
   * Generates a timed deadlock freedom assertion body.
   *
   * @param proc CSP-M for the process to check for timed deadlock freedom.
   * @return the body of the timed deadlock freedom assertion (eg, missing 'assert' or 'assert
   * not').
   */
  private CharSequence generateTimedDeadlockFreedom(CharSequence proc) {
    final var cs = csp.sets();
    final var cb = csp.bins();

    final var tockset = cs.tock();

    final var body = csp.renaming().rename("tock", "tock").rename("tock", "tock'").in(proc);
    final var pproc = csp.prioritise(body, csp.function("diff", "Events", cs.set("tock'", "tock")),
        tockset);
    return csp.tauPrioritiseTock("%s :[divergence free [FD]]".formatted(cb.hide(pproc, tockset)));
  }

  private CharSequence generateFDRBuiltin(CorePropertyType t) {
    // Note that the DEADLOCK_FREE reached here is the classic FDR
    // deadlock freedom, which is not the one we expose as a core
    // assertion (it requires timelock as well as deadlock).
    // It exists here mainly because we use it for termination checking.
    return switch (t) {
      case DETERMINISM -> "deterministic";
      case DEADLOCK_FREE -> "deadlock free";
      default -> throw new IllegalArgumentException(
          "core property type can't be a FDR builtin: %s".formatted(t));
    };
  }

}
