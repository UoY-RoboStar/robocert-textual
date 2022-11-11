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

import robostar.robocert.UntilFragment;
import robostar.robocert.textual.generator.tikz.matrix.CombinedFragmentRow;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.message.Set;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.Renderable;

/**
 * Frame representing an {@link UntilFragment}.
 *
 * @param fragment fragment being represented.
 * @param id   ID of the entry event for this frame.
 *
 * @author Matt Windsor
 */
public record UntilFrame(UntilFragment fragment, int id) implements Frame {

  @Override
  public Row row(EventType type) {
    return new CombinedFragmentRow(type, id);
  }

  @Override
  public String generateLabel(Renderable.Context ctx) {
    final var messages = new Set(fragment.getIntraMessages()).render(ctx);

    return ctx.tikz().command("rcanyuntil").argument(messages).render();
  }
}
