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

import circus.robocalc.robochart.NamedElement;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.CombinedFragment;
import robostar.robocert.ComponentActor;
import robostar.robocert.Interaction;
import robostar.robocert.TargetActor;
import robostar.robocert.textual.generator.tikz.frame.FrameGenerator;
import robostar.robocert.textual.generator.tikz.frame.NestedFrame;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.ActorColumn;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Diagram;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Edge;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Row;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.Event;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EventType;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Assembles an intermediate form of the TikZ diagram, ready to be turned into code.
 *
 * @author Matt Windsor
 */
public record DiagramStateBuilder(FrameGenerator frameGen, Interaction it, List<Actor> actors) {
  // TODO: decouple this more from the formatting of the code.

  /**
   * Builds intermediate diagram state.
   *
   * @return the built state, ready to be formatted into TikZ code.
   */
  public State build() {
    final var unwound = new InteractionUnwinder(it).unwind();

    final var matrixRows = new ArrayList<List<Cell>>();
    final var frames = new ArrayList<NestedFrame>();

    for (var entry : unwound.entries()) {
      matrixRowCells(entry).map(Stream::toList).ifPresent(matrixRows::add);
      frameGen.generate(entry).ifPresent(frames::add);
    }
    return new State(matrixRows, frames, unwound.maxDepth());
  }

  private Optional<Stream<Cell>> matrixRowCells(Event entry) {
    return Optional.ofNullable(new RowSwitch(entry.type(), entry.id()).doSwitch(entry.subject()));
  }

  private class RowSwitch extends RoboCertSwitch<Stream<Cell>> {

    private final EventType type;
    private final int id;

    public RowSwitch(EventType type, int id) {
      super();
      this.type = type;
      this.id = id;
    }

    @Override
    public Stream<Cell> caseInteraction(Interaction object) {
      final var row = new CellLocation.Diagram(type);

      // Construct the actor nodes on the top row of the diagram.
      final var actorCells = actors.stream().map(x -> actorCell(x, row));

      final var left = Cell.at(row, Edge.Gutter);
      final var right = Cell.at(row, Edge.World);

      return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
    }

    @Override
    public Stream<Cell> caseCombinedFragment(CombinedFragment object) {
      final var row = new CellLocation.CombinedFragment(type, id);

      final var actorCells = actors.stream().map(a -> Cell.at(row, new ActorColumn(a)));

      final var left = Cell.at(row, Edge.Gutter);
      final var right = Cell.at(row, Edge.World);

      return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
    }
  }

  private Cell actorCell(Actor actor, Row row) {
    final var cell = Cell.at(row, new ActorColumn(actor));

    // TODO: push this logic inwards.
    if (row instanceof Diagram d && d.type() == EventType.Entered) {
      return cell.setBodyFunction(_tikz -> actorText(actor)).setStyleFunction(_tikz -> "rcactor");
    }

    return cell;
  }


  private String actorText(Actor actor) {
    return "%s %s".formatted(actorStereotype(actor), actor.getName());
  }

  private static String actorStereotype(Actor actor) {
    if (actor instanceof TargetActor) {
      return "\\rctarget{}";
    } else if (actor instanceof ComponentActor c) {
      final var cnode = c.getNode();
      final var cname = cnode instanceof NamedElement n ? n.getName() : cnode.toString();
      return "\\rccomponent{%s}".formatted(cname);
    }
    return "(unknown)";
  }


  /**
   * Diagram state produced by this intermediate step.
   *
   * @param matrixRows      rows to place in the diagram's matrix.
   * @param frames          frames to render on top of the diagram's matrix.
   * @param outerDepthScale amount by which we should scale the outer frame's margin.
   */
  public record State(List<List<Cell>> matrixRows, List<NestedFrame> frames, int outerDepthScale) {

  }
}
