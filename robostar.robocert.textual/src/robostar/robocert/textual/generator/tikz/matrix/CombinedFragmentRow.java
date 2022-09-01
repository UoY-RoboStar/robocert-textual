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
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;

/**
 * A row in a matrix that represents the start or end of a combined fragment.
 *
 * @param type whether this entry into or exit out of a diagram (other types are not permitted).
 * @param id   numeric ID of this combined fragment.
 * @author Matt Windsor
 */
public record CombinedFragmentRow(EventType type, int id) implements Row {

  public CombinedFragmentRow {
    Objects.requireNonNull(type);
  }

  @Override
  public String rowName() {
    return "cf_%d_%s".formatted(id, type.toString());
  }
}
