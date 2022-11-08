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
import robostar.robocert.util.RoboCertSwitch;

/**
 * Generates matrix rows from flattened interaction events.
 * <p>
 * Generally, matrix rows capture the start of a combined fragment, the end of a combined fragment,
 * a branching spot between two interaction operands, or an occurrence fragment.  Of these, only
 * certain occurrences (and the actor lifeline headings produced at the start of an interaction)
 * generate nodes in the matrix; others generate coordinates from which we produce frames and other
 * adornments.
 *
 * @author Matt Windsor
 */
public class RowGenerator {

  /**
   * Generates a row as a list of cells.
   *
   * @param event  event for which we are generating a row.
   * @param actors list of non-World actors participating in the interaction.
   * @return the row, if one is indeed generatable for this event.
   */
  public Optional<List<Cell>> generate(Event event, List<Actor> actors) {
    return Optional.ofNullable(new RoboCertSwitch<Stream<Cell>>() {
      @Override
      public Stream<Cell> caseInteraction(Interaction object) {
        return makeRow(new DiagramRow(event.type()));
      }

      @Override
      public Stream<Cell> caseCombinedFragment(CombinedFragment object) {
        return makeRow(new CombinedFragmentRow(event.type(), event.id()));
      }

      @Override
      public Stream<Cell> caseInteractionOperand(InteractionOperand object) {
        // Emit branch separators if this is an entry into an interaction operand that is not the
        // first such operand in its parent.
        return event.isBranchSplit() ? makeRow(new BranchRow(event.id())) : null;
      }

      private Stream<Cell> makeRow(Row row) {
        final var actorCells = actors.stream().map(a -> new Cell(row, new ActorColumn(a)));

        final var left = new Cell(row, EdgeColumn.Gutter);
        final var right = new Cell(row, EdgeColumn.World);

        return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
      }
    }.doSwitch(event.subject())).map(Stream::toList);
  }

}
