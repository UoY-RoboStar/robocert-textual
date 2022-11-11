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
 * A row in a matrix that represents an occurrence.
 *
 * @param id   numeric ID of this occurrence.
 * @author Matt Windsor
 */
public record OccurrenceRow(int id) implements Row {
  @Override
  public String rowName() {
    return "occ_%d".formatted(id);
  }

  @Override
  public Optional<CellBody> generateBody(Column column) {
    // Generate all cells in the column.
    // (Technically we don't need the LHS, but it's harmless to generate it.)
    return Optional.of(new CoordinateCellBody());
  }
}
