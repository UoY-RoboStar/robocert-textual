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

import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.EdgeColumn;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Pairs a {@link Frame} with its nesting depth.
 *
 * @param frame frame being paired with its depth.
 * @param depth depth of the given frame.
 * @author Matt Windsor
 */
public record NestedFrame(Frame frame, int depth) implements Renderable {

  @Override
  public String render(Renderable.Context ctx) {
    final var label = frame.generateLabel(ctx);
    return ctx.tikz().command("rcframe").argument(Integer.toString(ctx.topLevel() - depth))
        .argument(new Cell(frame.row(EventType.Entered), EdgeColumn.Gutter).name())
        .argument(new Cell(frame.row(EventType.Exited), EdgeColumn.World).name()).argument(label)
        .render();
  }
}
