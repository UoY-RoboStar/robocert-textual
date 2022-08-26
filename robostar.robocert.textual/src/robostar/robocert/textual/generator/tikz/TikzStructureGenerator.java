/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 */

package robostar.robocert.textual.generator.tikz;

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
    return "\\node[%s](%s){%s};".formatted(style, name, content);
  }

  /**
   * Emits TikZ code for a coordinate.
   *
   * @param name name of the coordinate.
   * @return a string representing the TikZ coordinate code.
   */
  public String coordinate(String name) {
    return "\\coordinate(%s);".formatted(name);
  }

}
