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
 * Handles generation of pieces of low-level TikZ code.
 *
 * @author Matt Windsor
 */
public class TikzStructureGenerator {

  /**
   * Emits TikZ code for a node.
   *
   * @param style   styling information for the node.
   * @param name    name of the node.
   * @param content content for the label of the node.
   * @return a string representing the TikZ node code.
   */
  public String node(String style, String name, String content) {
    return command("node").optional(style).node(name).argument(content).render();
  }

  /**
   * Emits TikZ code for a coordinate.
   *
   * @param name name of the coordinate.
   * @return a string representing the TikZ coordinate code.
   */
  public String coordinate(String name) {
    return command("coordinate").node(name).render();
  }

  /**
   * Opens a builder for creating a command.
   *
   * @param name name of the command.
   * @return a command builder.
   */
  public Command command(String name) {
    return new Command(name);
  }

  /**
   * Opens a builder for creating a path.
   *
   * @param style style of the path.
   * @return a path builder.
   */
  public Path draw(String style) {
    return new Path(this, style);
  }
}
