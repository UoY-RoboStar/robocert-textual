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

import robostar.robocert.CombinedFragment;
import robostar.robocert.textual.generator.tikz.matrix.CombinedFragmentRow;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Placeholder frame for a type of combined fragment not yet supported by the generator.
 * <p>
 * If this appears in a TikZ diagram, it's a canary that some combined fragment was picked up by the
 * diagram builder but not understood by it.
 *
 * @param fragment fragment generating this frame.
 * @param id       ID of the entry event for this frame.
 * @author matt Windsor
 */
public record MissingFrame(CombinedFragment fragment, int id) implements Frame {

  @Override
  public Row row(EventType type) {
    return new CombinedFragmentRow(type, id);
  }

  @Override
  public String generateLabel(TikzStructureGenerator tikz) {
    return "unknown (%s) %d".formatted(fragment.toString(), id);
  }
}
