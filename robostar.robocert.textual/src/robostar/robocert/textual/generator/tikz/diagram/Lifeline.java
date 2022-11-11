/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.diagram;

import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.Actor;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tikz.matrix.ActorColumn;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.DiagramRow;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * An actor lifeline.
 *
 * @param actor actor forming this lifeline.
 */
public record Lifeline(Actor actor) implements Renderable {
  public Lifeline {
    if (actor instanceof World) {
      throw new IllegalArgumentException("lifeline cannot be a world");
    }
  }

  @Override
  public String render(Renderable.Context ctx) {
    final var col = new ActorColumn(actor);
    final var start = Cell.nameOf(new DiagramRow(EventType.Entered), col);
    final var end = Cell.nameOf(new DiagramRow(EventType.Exited), col);
    return ctx.tikz().draw("rclifeline").to(start).to(end).render();
  }
}
