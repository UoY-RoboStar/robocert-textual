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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder for basic TikZ paths.
 *
 * @author Matt Windsor
 */
public class Path {

  private final String style;
  private final TikzStructureGenerator tikz;
  private final List<String> waypoints = new ArrayList<>();

  /**
   * Constructs a TikZ path builder.
   *
   * @param tikz  low-level structure generator.
   * @param style styling information for the path.
   */
  public Path(TikzStructureGenerator tikz, String style) {
    this.tikz = tikz;
    this.style = style;
  }

  public Path to(String node) {
    waypoints.add(node);
    return this;
  }

  /**
   * Renders this path as TikZ.
   *
   * @return TikZ representing the rendered path.
   */
  public String render() {
    final var header = tikz.command("draw").optional(style).render();
    return waypoints.stream().map("(%s)"::formatted)
        .collect(Collectors.joining(" -- ", header, ";"));
  }
}
