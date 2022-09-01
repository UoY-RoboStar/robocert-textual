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
 * Interface of things that represent rows in a TikZ sequence diagram matrix.
 *
 * @author Matt Windsor
 * @see Column
 * @see CellLocation
 */
public interface Row {

  /**
   * Gets the name stub that this row should append into the node name.
   *
   * @return generated name.
   */
  String rowName();
}
