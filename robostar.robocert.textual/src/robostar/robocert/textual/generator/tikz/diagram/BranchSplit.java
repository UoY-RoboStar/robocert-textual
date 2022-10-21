/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.diagram;

import robostar.robocert.textual.generator.tikz.matrix.BranchRow;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.EdgeColumn;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Represents a point where two branches split in a diagram.
 *
 * @param id    ID of the second branch in the split.
 * @param depth depth the branches, used to work out where to start and stop drawing the line.
 */
public record BranchSplit(int id, int depth) {

  /**
   * Renders a branch split line.
   *
   * @param tikz     low-level Tikz structural generator.
   * @param maxDepth depth of the innermost item in the diagram.
   * @return TikZ code for the split line.
   */
  public String render(TikzStructureGenerator tikz, int maxDepth) {
    final var depthOffset = (maxDepth - depth) + 1;

    final var startName = Cell.nameOf(new BranchRow(id), EdgeColumn.Gutter);
    final var start = nudge(startName, -depthOffset);

    final var endName = Cell.nameOf(new BranchRow(id), EdgeColumn.World);
    final var end = nudge(endName, depthOffset);

    return tikz.draw("rcsep").to(start).to(end).render();
  }

  private String nudge(String nodeName, int amount) {
    return "$(%s) + (%d*\\the\\rcstepmargin, 0)$".formatted(nodeName, amount);
  }
}