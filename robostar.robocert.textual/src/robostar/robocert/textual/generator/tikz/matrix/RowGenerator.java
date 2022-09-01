/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.matrix;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.CombinedFragment;
import robostar.robocert.Interaction;
import robostar.robocert.InteractionOperand;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.Event;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Generates matrix rows from flattened interaction events.
 *
 * @author Matt Windsor
 */
public class RowGenerator {

  /**
   * Generates a row as a list of cells.
   *
   * @param entry  entry for which we are generating a row.
   * @param actors list of non-World actors participating in the interaction.
   * @return the row, if one is indeed generatable for this event.
   */
  public Optional<List<Cell>> generate(Event entry, List<Actor> actors) {
    return Optional.ofNullable(
        new Switch(entry.type(), entry.id(), entry.relativeId(), actors).doSwitch(entry.subject())).map(Stream::toList);
  }

  private static class Switch extends RoboCertSwitch<Stream<Cell>> {

    private final EventType type;
    private final int id;
    private final int relativeId;
    private final List<Actor> actors;

    public Switch(EventType type, int id, int relativeId, List<Actor> actors) {
      super();
      this.type = type;
      this.id = id;
      this.relativeId = relativeId;
      this.actors = actors;
    }

    @Override
    public Stream<Cell> caseInteraction(Interaction object) {
      return makeRow(new DiagramRow(type));
    }

    @Override
    public Stream<Cell> caseCombinedFragment(CombinedFragment object) {
      return makeRow(new CombinedFragmentRow(type, id));
    }

    @Override
    public Stream<Cell> caseInteractionOperand(InteractionOperand object) {
      // Emit branch separators if this is an entry into an interaction operand that is not the
      // first such operand in its parent.
      final var isBranchSep = type == EventType.Entered && 0 < relativeId;
      return isBranchSep ? makeRow(new BranchRow(id)) : null;
    }

    private Stream<Cell> makeRow(Row row) {
      final var actorCells = actors.stream().map(a -> new Cell(row, new ActorColumn(a)));

      final var left = new Cell(row, EdgeColumn.Gutter);
      final var right = new Cell(row, EdgeColumn.World);

      return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
    }
  }
}
