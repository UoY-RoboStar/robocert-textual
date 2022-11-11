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
import robostar.robocert.textual.generator.tikz.util.Renderable;

/**
 * Represents a point where two branches split in a diagram.
 *
 * @param id    ID of the second branch in the split.
 * @param depth depth the branches, used to work out where to start and stop drawing the line.
 */
public record BranchSplit(int id, int depth) implements Renderable {

  @Override
  public String render(Renderable.Context ctx) {
    final var startCell = new Cell(new BranchRow(id), EdgeColumn.Gutter);
    final var start = ctx.nestedEdgeCellName(startCell, depth - 1);

    final var endCell = new Cell(new BranchRow(id), EdgeColumn.World);
    final var end = ctx.nestedEdgeCellName(endCell, depth - 1);

    return ctx.tikz().draw("rcsep").to(start).to(end).render();
  }

}
