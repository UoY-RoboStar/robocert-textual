/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.matrix;

import java.util.Optional;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * A cell in a RoboCert sequence diagram matrix.
 *
 * @author Matt Windsor
 */
public record Cell(Row row, Column column) {
  /**
   * Shorthand for constructing a cell then immediately taking its name.
   *
   * @param row row of the node.
   * @param column column of the node.
   * @return constructed name of the node or coordinate.
   */
  public static String nameOf(Row row, Column column) {
    return new Cell(row, column).name();
  }


  /**
   * Generates TikZ for this matrix cell.
   * <p>
   *   If either the location or the body is null, we don't generate any cell code.
   *
   * @param tikz low-level TikZ structure generator.
   * @return TikZ code for the cell (either a node or a coordinate), if one has been generated.
   */
  public Optional<String> render(TikzStructureGenerator tikz) {
    return row.generateBody(column).map(body -> {
      final var locName = name();

      final var label = body.renderLabel(tikz);
      if (label.isEmpty()) {
        return tikz.coordinate(locName);
      }
      final var style = body.renderStyle(tikz).orElse("");
      return tikz.node(style, locName, label.get());
    });
  }

  /**
   * Gets the name of this cell.
   * @return programmatically generated name based on row and column names.
   */
  public String name() {
    return String.join("_", row.rowName(), column.columnName());
  }
}
