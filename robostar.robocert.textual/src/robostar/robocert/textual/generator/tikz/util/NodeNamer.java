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

import robostar.robocert.Actor;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EntryType;

/**
 * Handles the naming conventions used in RoboCert TikZ diagrams for nodes.
 * <p>
 * Connections between nodes, as well as relative positioning thereof, requires common conventions
 * for node naming, which this class enforces.
 * <p>
 * Node names have two components: one for the row of the node in the matrix, another for the
 * column.  Different types of row and column exist to accommodate different types of node.
 *
 * @author Matt Windsor
 */
public class NodeNamer {

  /**
   * Shorthand for constructing a diagram node.
   *
   * @param type   type (entry or exit) of the node.
   * @param column column of the node.
   * @return constructed name of the node or coordinate.
   */
  public String diagram(EntryType type, Column column) {
    return node(new Diagram(type), column);
  }

  /**
   * Constructs a node or coordinate name for a given row and column.
   *
   * @param row    row of the node.
   * @param column column of the node.
   * @return constructed name of the node or coordinate.
   */
  public String node(Row row, Column column) {
    return String.join("_", row.rowName(), column.columnName());
  }

  /**
   * Interface of things that represent rows in a TikZ sequence diagram matrix.
   *
   * @author Matt Windsor
   */
  public interface Row {

    /**
     * Gets the name stub that this row should append into the node name.
     *
     * @return generated name.
     */
    String rowName();
  }

  /**
   * A row in a matrix that represents the start or end of a combined fragment.
   *
   * @param type whether this entry into or exit out of a diagram (other types are not permitted).
   * @param id   numeric ID of this combined fragment.
   * @author Matt Windsor
   */
  public record CombinedFragment(EntryType type, int id) implements Row {

    public CombinedFragment {
      if (type != EntryType.Entered && type != EntryType.Exited) {
        throw new IllegalArgumentException(
            "unexpected entry type in diagram row: %s".formatted(type));
      }
    }

    @Override
    public String rowName() {
      return "cf_%d_%s".formatted(id, type.toString());
    }
  }

  /**
   * A row in a matrix that represents the start or end of a diagram.
   *
   * @param type whether this entry into or exit out of a diagram (other types are not permitted).
   * @author Matt Windsor
   */
  public record Diagram(EntryType type) implements Row {

    public Diagram {
      if (type != EntryType.Entered && type != EntryType.Exited) {
        throw new IllegalArgumentException(
            "unexpected entry type in diagram row: %s".formatted(type));
      }
    }

    @Override
    public String rowName() {
      return "diagram_" + type.toString();
    }
  }

  /**
   * Interface of things that represent columns in a TikZ sequence diagram matrix.
   *
   * @author Matt Windsor
   */
  public interface Column {

    /**
     * Gets the name stub that this column should append into the node name.
     *
     * @return generated name.
     */
    String columnName();
  }

  /**
   * Represents one of the edge columns in a TikZ sequence diagram matrix.
   *
   * @author Matt Windsor
   */
  public enum Edge implements Column {
    /**
     * The side of a sequence diagram that is not representing the world.
     */
    Gutter,
    /**
     * The side of a sequence diagram that is representing the world.
     */
    World;

    @Override
    public String columnName() {
      return switch (this) {
        case Gutter -> "g";
        case World -> "w";
      };
    }
  }

  /**
   * Encapsulates an {@link Actor} in a {@link Column}, allowing the {@link NodeNamer} to handle
   * it.
   *
   * @param actor actor to be wrapped.
   */
  public record ActorColumn(Actor actor) implements Column {

    @Override
    public String columnName() {
      return "a" + actor.getName();
    }
  }
}
