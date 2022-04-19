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

package robocalc.robocert.generator.tockcsp.ll.csp;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for generating CSP modules.
 *
 * @author Matt Windsor
 */
public class Module {

  private final CharSequence name;
  private final List<CharSequence> privateParts = new ArrayList<>();
  private final List<CharSequence> publicParts = new ArrayList<>();

  /**
   * Creates a new module.
   *
   * @param name the name of the module.
   */
  public Module(CharSequence name) {
    this.name = name;
  }

  /**
   * Adds content to the private part of this module.
   *
   * @param xs the list of character sequences to add to this module.
   * @return a reference to this module.
   */
  public Module withPrivate(CharSequence... xs) {
    this.privateParts.addAll(List.of(xs));
    return this;
  }

  /**
   * Adds content to the public part of this module.
   *
   * @param xs the list of character sequences to add to this module.
   * @return a reference to this module.
   */
  public Module withPublic(CharSequence... xs) {
    this.publicParts.addAll(List.of(xs));
    return this;
  }

  /**
   * Ends the module and returns the resulting CSP-M.
   *
   * @return the generated CSP-M for this module.
   */
  public CharSequence end() {
    final var sb = new StringBuilder();
    sb.append("module ").append(name).append('\n');
    for (var priv : privateParts) {
      sb.append(CSPStructureGenerator.indentStrip(priv)).append('\n');
    }
    sb.append("exports\n");
    for (var pub : publicParts) {
      sb.append(CSPStructureGenerator.indentStrip(pub)).append('\n');
    }
    sb.append("endmodule");
    return sb.toString();
  }
}
