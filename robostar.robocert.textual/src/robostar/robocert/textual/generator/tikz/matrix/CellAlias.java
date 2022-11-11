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

import robostar.robocert.textual.generator.tikz.util.Renderable;

/**
 * Represents an alias from one cell to another.
 * <p>
 * We use these to model areas where we've removed redundant rows in matrix processing.
 *
 * @param from previous node name.
 * @param to   new node name.
 * @author Matt Windsor
 */
public record CellAlias(Cell from, Cell to) implements Renderable {

  @Override
  public String render(Context ctx) {
    return ctx.tikz().command("pgfnodealias").argument(to.name()).argument(from.name()).render();
  }
}
