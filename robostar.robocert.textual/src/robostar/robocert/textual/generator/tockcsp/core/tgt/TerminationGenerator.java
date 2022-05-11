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
 *   Alvaro Miyazawa and Pedro Ribeiro - initial definition in RoboChart
 *   Matt Windsor - port to RoboCert
 ******************************************************************************/

package robostar.robocert.textual.generator.tockcsp.core.tgt;

import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;

/**
 * Handles the generation of references to the 'terminate' channel, sets containing the channel, and
 * attempts to hide it.
 *
 * <p>This class exists because lots of different parts of the target generation logic need it.
 *
 * @param csp the low-level CSP generator.
 * @author Matt Windsor
 */
public record TerminationGenerator(CSPStructureGenerator csp) {

  /**
   * Constructs a termination generator.
   * @param csp the low-level CSP generator.
   */
  @Inject
  public TerminationGenerator {
    Objects.requireNonNull(csp);
  }

  /**
   * Constructs a reference to the termination channel.
   *
   * @param ns the namespace of the target element.
   * @return the terminate channel (not in a set).
   */
  public CharSequence terminate(CharSequence ns) {
    return csp.namespaced(ns, "terminate");
  }

  /**
   * Constructs a set over a reference to the termination channel.
   *
   * @param ns the namespace of the target element.
   * @return the terminate channel (not in a set).
   */
  private CharSequence terminateSet(CharSequence ns) {
    return csp.sets().set(terminate(ns));
  }

  /**
   * Hides the termination channel inside a CSP-M process.
   *
   * @param ns   the namespace of the target element.
   * @param body the body in which the channel should be hidden.
   * @return the result of hiding {@code terminateSet(ns)} in {@code body}.
   */
  public CharSequence hideTerminate(CharSequence ns, CharSequence body) {
    return csp.bins().hide(body, terminateSet(ns));
  }

  /**
   * Sets up termination inside a CSP process.
   * <p>
   * This consists of an interrupt over the termination channel, followed by a hide of the same
   * channel.
   *
   * @param ns   the namespace of the target element.
   * @param body the body in which the channel should be hidden.
   * @return the result of hiding {@code terminateSet(ns)} in {@code body}.
   */
  public CharSequence handleTerminate(CharSequence ns, CharSequence body) {
    final var terminate = terminateSet(ns);
    final var terminated = csp.bins().interrupt(csp.tuple(body), terminate, csp.skip());

    // Could use hideTerminate, but then we'd be generating `terminate` twice.
    // (Bit of a micro-optimisation, mind.)
    return csp.bins().hide(terminated, terminate);
  }

}
