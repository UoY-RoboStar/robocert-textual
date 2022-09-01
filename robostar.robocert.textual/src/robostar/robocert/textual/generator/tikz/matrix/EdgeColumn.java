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

/**
 * Represents one of the edge columns in a TikZ sequence diagram matrix.
 *
 * @author Matt Windsor
 */
public enum EdgeColumn implements Column {
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
