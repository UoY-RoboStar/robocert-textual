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

import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;

/**
 * Represents a cell location in a TikZ sequence diagram matrix.
 * <p>
 * Connections between nodes, as well as relative positioning thereof, requires common conventions
 * for cell node naming, which this class enforces.
 * <p>
 * Locations have two components: one for the row of the node in the matrix, another for the column.
 * Different types of row and column exist to accommodate different types of node.
 *
 * @param row    row of the node.
 * @param column column of the node.
 * @author Matt Windsor
 * @see Row
 * @see Column
 */
public record CellLocation(Row row, Column column) {

  /**
   * Shorthand for constructing a diagram node.
   *
   * @param type   type (entry or exit) of the node.
   * @param column column of the node.
   * @return constructed name of the node or coordinate.
   */
  public static CellLocation diagram(EventType type, Column column) {
    return new CellLocation(new DiagramRow(type), column);
  }

  /**
   * Constructs a node or coordinate name for a given row and column.
   *
   * @return constructed name of the node or coordinate.
   */
  public String name() {
    return String.join("_", row.rowName(), column.columnName());
  }

}
