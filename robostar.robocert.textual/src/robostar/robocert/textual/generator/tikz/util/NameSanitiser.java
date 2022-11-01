/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.util;

/**
 * Sanitises names into a LaTeXable form.
 *
 * @author Matt Windsor
 */
public class NameSanitiser {

  /**
   * Sanitises a string ready for LaTeX consumption
   *
   * @param raw raw string to sanitise.
   * @return sanitised string.
   */
  public static String sanitise(String raw) {
    // Needed because LaTeX usually expects _ to be in math mode.
    return raw.replace("_", "\\_");
  }
}
