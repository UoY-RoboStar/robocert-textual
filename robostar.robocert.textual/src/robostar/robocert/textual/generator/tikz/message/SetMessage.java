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

import robostar.robocert.textual.generator.tikz.util.Renderable;

/**
 * Represents a message inside a message set.
 *
 * @param msg message to be rendered into TikZ.
 * @author Matt Windsor
 */
public record SetMessage(robostar.robocert.Message msg) implements Renderable {

  @Override
  public String render(Renderable.Context ctx) {
    final var from = msg.getFrom().getName();
    final var to = msg.getTo().getName();
    final var topic = new Topic(msg.getTopic()).render(ctx);
    final var args = new MessageArgumentList(msg.getArguments()).render(ctx);

    return ctx.tikz().command("rcsetmessage").argument(from).argument(to).argument(topic).argument(args)
        .render();
  }
}
