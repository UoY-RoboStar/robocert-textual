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
 *   $author - initial definition
 ******************************************************************************/

package robostar.robocert.textual.generator.tockcsp.ll.csp;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Helper class for collecting and manipulating streams of CSP.
 *
 * @param csp low-level CSP helper.
 *
 * @author Matt Windsor
 */
public record CSPStreamHelper(CSPStructureGenerator csp) {
  /**
   * Constructs a CSP stream helper.
   *
   * @param csp low-level CSP helper.
   */
  @Inject
  public CSPStreamHelper {
    Objects.requireNonNull(csp);
  }

  /**
   * Joins a stream of character sequences with newlines, then wraps the result in a
   * module only if the resulting sequence is non-empty.
   *
   * @param name the name of the module to produce.
   * @param isTimed whether to wrap the body into a timed section.
   *
   * @return an empty Stream if there is no module; else, a singleton Stream containing the
   * character sequence.
   */
  public Collector<CharSequence, ?, Optional<CharSequence>> collectToModule(
      CharSequence name, boolean isTimed) {
    return Collectors.collectingAndThen(Collectors.joining("\n"), x -> moduleIfNonEmpty(x, name, isTimed));
  }

  private Optional<CharSequence> moduleIfNonEmpty(CharSequence mod,
      CharSequence name, boolean isTimed) {
    if (mod.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(csp.module(name).withPublic(csp.timedIf(isTimed, mod)).end());
  }
}
