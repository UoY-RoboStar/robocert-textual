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
import robostar.robocert.textual.generator.tikz.matrix.CombinedFragmentRow;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Frame representing a basic combined fragment with no arguments.
 *
 * @param type type of frame.
 * @param id   ID of the entry event for this frame.
 * @author Matt Windsor
 */
public record BasicFrame(Type type, int id) implements Frame {

  /**
   * Enumeration of types of basic frame.
   */
  public enum Type {
    /**
     * An 'alt' frame.
     */
    Alt,
    /**
     * An 'opt' frame.
     */
    Opt,
    /**
     * An 'xalt' frame.
     */
    XAlt;

    @Override
    public String toString() {
      return switch (this) {
        case Alt -> "alt";
        case Opt -> "opt";
        case XAlt -> "xalt";
      };
    }
  }

  @Override
  public Row row(EventType type) {
    return new CombinedFragmentRow(type, id);
  }

  @Override
  public String generateLabel(Renderable.Context ctx) {
    return ctx.tikz().command("rc%s".formatted(type)).render();
  }
}
