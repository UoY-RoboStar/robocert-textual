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
 * A row in a matrix that represents the start or end of a diagram.
 *
 * @param type whether this entry into or exit out of a diagram (other types are not permitted).
 * @author Matt Windsor
 */
public record DiagramRow(EventType type) implements Row {

  public DiagramRow {
    Objects.requireNonNull(type);
  }

  @Override
  public String rowName() {
    return "diagram_" + type.toString();
  }
}
