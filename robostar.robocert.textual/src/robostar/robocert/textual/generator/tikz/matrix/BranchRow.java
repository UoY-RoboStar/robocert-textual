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

/**
 * A row in a matrix that represents a division between two branches.
 *
 * @param id   numeric ID of the lower branch.
 * @author Matt Windsor
 */
public record BranchRow(int id) implements Row {

  @Override
  public String rowName() {
    return "branch_%d".formatted(id);
  }

  @Override
  public CellBody generateBody(Column column) {
    // Only generate coordinates for endpoints, not actors.
    return new CoordinateCellBody();
  }
}
