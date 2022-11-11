/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.message;

import robostar.robocert.Actor;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.Temperature;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tikz.matrix.ActorColumn;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.Column;
import robostar.robocert.textual.generator.tikz.matrix.EdgeColumn;
import robostar.robocert.textual.generator.tikz.matrix.OccurrenceRow;
import robostar.robocert.textual.generator.tikz.util.Renderable;

/**
 * Represents a message on a lifeline.
 *
 * @param msgOcc message occurrence to be rendered into TikZ.
 * @param id     ID of the entry event for this message.
 * @author Matt Windsor
 */
public record LifelineMessage(MessageOccurrence msgOcc, int id) implements Renderable {

  @Override
  public String render(Context ctx) {
    // TODO(@MattWindsor91): sync/async messages
    // TODO(@MattWindsor91): deduplicate with SetMessage somehow?

    final var msg = msgOcc.getMessage();
    final var from = cellName(msg.getFrom());
    final var to = cellName(msg.getTo());
    final var topic = new Topic(msg.getTopic()).render(ctx);
    final var args = new MessageArgumentList(msg.getArguments()).render(ctx);
    final var temp = msgOcc.getTemperature() == Temperature.COLD ? "rccold" : "rchot";

    return ctx.tikz().command("rcmessage").argument(from).argument(to).argument(topic)
        .argument(args).argument(temp).render();
  }

  private String cellName(Actor a) {
    return Cell.nameOf(new OccurrenceRow(id), actorColumn(a));
  }

  private Column actorColumn(Actor a) {
    // Make sure we place world message endpoints on the world edge of the diagram.
    if (a instanceof World) {
      return EdgeColumn.World;
    }
    return new ActorColumn(a);
  }
}
