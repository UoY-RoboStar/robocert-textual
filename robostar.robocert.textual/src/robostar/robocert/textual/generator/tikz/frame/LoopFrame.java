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

import com.google.common.base.Strings;
import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.DiscreteBound;
import robostar.robocert.LoopFragment;
import robostar.robocert.textual.generator.tikz.matrix.CombinedFragmentRow;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.util.Command;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.NameSanitiser;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * A frame representing a loop fragment.
 *
 * @param loop fragment being represented.
 * @param id   ID of the entry event for this frame.
 * @author Matt Windsor
 */
public record LoopFrame(LoopFragment loop, int id) implements Frame {

  @Override
  public Row row(EventType type) {
    return new CombinedFragmentRow(type, id);
  }

  @Override
  public String generateLabel(TikzStructureGenerator tikz, ISerializer ser) {
    return labelCommand(tikz).argument(bound(ser, loop.getBound())).render();
  }

  private Command labelCommand(TikzStructureGenerator tikz) {
    final var name = loop.getName();
    if (Strings.isNullOrEmpty(name)) {
      return tikz.command("rcloop");
    }
    return tikz.command("rcnamedloop").argument(NameSanitiser.sanitise(name));
  }

  public String bound(ISerializer ser, DiscreteBound bound) {
    if (bound == null) {
      return "";
    }
    final var lo = bound.getLower();
    final var hi = bound.getUpper();

    final var hiStr = hi == null ? "*" : ser.serialize(hi);

    if (lo == null) {
      return hi == null ? "" : "(%s)".formatted(hiStr);
    } else {
      return "(%s, %s)".formatted(ser.serialize(lo), hiStr);
    }
  }
}
