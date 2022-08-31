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

import robostar.robocert.textual.generator.tikz.matrix.CellLocation;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Edge;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EventType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Pairs a {@link Frame} with its nesting depth.
 *
 * @param frame frame being paired with its depth.
 * @param depth depth of the given frame.
 * @author Matt Windsor
 */
public record NestedFrame(Frame frame, int depth) {

  /**
   * Renders this frame as TikZ code.
   *
   * @param tikz     low-level structure generator for TikZ.
   * @param topLevel depth of the highest frame, used to scale the nesting level argument.
   * @return a string containing TikZ code for this frame.
   */
  public String render(TikzStructureGenerator tikz, int topLevel) {
    final var label = frame.generateLabel(tikz);
    return tikz.command("rcframe").argument(Integer.toString(topLevel - depth))
        .argument(new CellLocation(frame.row(EventType.Entered), Edge.Gutter).name())
        .argument(new CellLocation(frame.row(EventType.Exited), Edge.World).name()).argument(label)
        .render();
  }
}
