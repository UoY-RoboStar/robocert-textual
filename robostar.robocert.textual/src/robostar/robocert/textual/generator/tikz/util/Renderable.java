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

import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.EdgeColumn;

/**
 * Interface representing a sequence diagram element that can be rendered to TikZ.
 *
 * @author Matt Windsor
 */
public interface Renderable {
  // TODO(@MattWindsor91): this seems awkwardly coupled.

  /**
   * Renders the element.
   *
   * @param ctx rendering context.
   * @return string of rendered TikZ.
   */
  String render(Context ctx);

  /**
   * Context for rendering.
   *
   * @param tikz     TikZ structure generator, used to create TikZ commands.
   * @param ser      serialiser used for rendering expressions.
   * @param topLevel nesting level of the highest-nested element, used for scaling purposes.
   */
  record Context(TikzStructureGenerator tikz, ISerializer ser, int topLevel) {

    /**
     * Gets the offset of the depth of something from the inmost depth level of the diagram.
     * @param depth depth for which we want an offset.
     * @return the calculated offset.
     */
    public int depthOffset(int depth) {
      // TODO(@MattWindsor91): off by one?
      return topLevel() - depth;
    }

    /**
     * Produces a TikZ coordinate for moving a coordinate left by multiples of the step margin.
     * @param nodeName existing coordinate.
     * @param offset the amount by which we are moving the coordinate.
     * @return TikZ for shifting the coordinate.
     */
    public String nudgeLeft(String nodeName, int offset) {
      return nudgeBy(nodeName, -depthOffset(offset));
    }

    /**
     * Adjusts the TikZ for an edge cell by accounting for nesting.
     *
     * @param cell cell whose coordinate we want to reference
     * @param offset the nesting level we want to adjust towards.
     * @return TikZ for the shifted coordinate.
     */
    public String nestedEdgeCellName(Cell cell, int offset) {
      final var baseName = cell.name();

      if (!(cell.column() instanceof EdgeColumn e)) {
        return baseName;
      }

      final var sign = e == EdgeColumn.Gutter ? -1 : 1;

      return nudgeBy(baseName, depthOffset(offset) * sign);
    }

    private String nudgeBy(String nodeName, int amount) {
      return "$(%s) + (%d*\\the\\rcstepmargin, 0)$".formatted(nodeName, amount);
    }
  }
}
