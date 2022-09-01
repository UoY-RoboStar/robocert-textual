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

import java.util.Objects;
import java.util.function.Function;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Builder for RoboCert sequence matrix cells.
 *
 * @author Matt Windsor
 */
public class Cell {

  private final CellLocation location;
  private Function<TikzStructureGenerator, String> styleFunction = Cell::dummyStyleFunction;
  private Function<TikzStructureGenerator, String> bodyFunction = null;

  /**
   * Constructs a cell.
   * <p>
   * This cell is a coordinate until and unless its body function is set.
   *
   * @param location location of the cell.
   */
  public Cell(CellLocation location) {
    this.location = location;
  }

  /**
   * Shorthand for constructing a cell at a row/column location.
   *
   * @param row    row of the cell.
   * @param column column of the cell.
   * @return the resulting coordinate cell.
   */
  public static Cell at(Row row, Column column) {
    return new Cell(new CellLocation(row, column));
  }

  /**
   * Sets the function to be used to create this cell's body.
   *
   * <p>
   * Setting this to non-null makes the cell generate a node rather than a coordinate.
   *
   * @param f function to use to generate the body.
   * @return this object.
   */
  public Cell setBodyFunction(Function<TikzStructureGenerator, String> f) {
    bodyFunction = f;
    return this;
  }

  /**
   * Sets the function to be used to create this cell's style.
   *
   * <p>
   * If the cell has no body, this function is ignored.
   *
   * @param f function to use to generate the style.
   * @return this object.
   */
  public Cell setStyleFunction(Function<TikzStructureGenerator, String> f) {
    styleFunction = Objects.requireNonNullElse(f, Cell::dummyStyleFunction);
    return this;
  }

  private static String dummyStyleFunction(TikzStructureGenerator tikz) {
    return "";
  }

  /**
   * Generates TikZ for this matrix cell.
   *
   * @param tikz low-level TikZ structure generator.
   * @return TikZ code for the cell (either a node or a coordinate).
   */
  public String generate(TikzStructureGenerator tikz) {
    if (location == null) {
      return "";
    }
    final var locName = location.name();

    if (bodyFunction == null) {
      return tikz.coordinate(locName);
    }

    final var style = styleFunction.apply(tikz);
    final var body = bodyFunction.apply(tikz);
    return tikz.node(style, locName, body);
  }
}
