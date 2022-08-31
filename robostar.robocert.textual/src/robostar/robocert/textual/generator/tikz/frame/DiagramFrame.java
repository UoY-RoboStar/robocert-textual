/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.frame;

import java.util.Objects;

import robostar.robocert.Interaction;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Diagram;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Row;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EventType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * A frame that represents the outer layer of a diagram.
 *
 * @param diagram diagram being represented by this frame.
 */
public record DiagramFrame(Interaction diagram) implements Frame {

  @Override
  public Row row(EventType type) {
    return new Diagram(type);
  }

  @Override
  public String generateLabel(TikzStructureGenerator tikz) {
    return tikz.command("rcseq").argument(diagram.getName()).argument(targetName()).render();
  }

  private String targetName() {
    final var group = diagram.getGroup();
    if (group == null) {
      return "(orphaned)";
    }
    final var targetBaseName = Objects.toString(group.getTarget(), "(no target)");
    return String.join("::", group.getName(), targetBaseName);
  }
}
