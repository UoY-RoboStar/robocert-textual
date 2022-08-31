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

import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.ActorColumn;
import robostar.robocert.textual.generator.tikz.matrix.CellLocation.Edge;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EntryType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Generates TikZ for one diagram.
 *
 * @param tikz TikZ structure generator.
 * @author Matt Windsor
 */
public record DiagramGenerator(TikzStructureGenerator tikz) {

  public static final String HEADING = """
      % Remember to \\input or import the baseline definitions for RoboCert TikZ files.
      % See the standalone .tex file for an example.
      """;

  @Inject
  public DiagramGenerator {
    Objects.requireNonNull(tikz);
  }

  /**
   * Generates TikZ for a diagram.
   *
   * @param it interaction diagram to generate.
   * @return the generated TikZ code (to include within TeX).
   */
  public CharSequence generate(Interaction it) {
    // We treat the World separately -- it always appears at the end of a row.
    final var actors = it.getActors().stream().filter(x -> !(x instanceof World)).toList();

    final var state = new DiagramStateBuilder(tikz, it, actors).build();

    final var matrix = state.matrixRows().stream().map(this::generateMatrixRow)
        .collect(Collectors.joining("\n", "\\matrix[rcseq]{\n", "\n};"));

    final var lifelines = actors.stream().map(this::lifeline).collect(Collectors.joining("\n"));

    final var targetName = String.join("::", it.getGroup().getName(),
        it.getGroup().getTarget().toString());

    final var frame = tikz.command("rcseqframe").argument(Integer.toString(state.outerDepthScale()))
        .argument(CellLocation.diagram(EntryType.Entered, Edge.Gutter).name())
        .argument(CellLocation.diagram(EntryType.Exited, Edge.World).name()).argument(targetName)
        .argument(it.getName()).build();

    return String.join("\n\n", HEADING, matrix, frame, lifelines);
  }

  private String generateMatrixRow(List<Cell> row) {
    return row.stream().map(x -> x.generate(tikz))
        .collect(Collectors.joining(" & ", "  ", " \\\\"));
  }

  /**
   * Constructs the TikZ command for drawing an actor's lifeline.
   *
   * @param actor actor for which we are drawing a lifeline.
   * @return TikZ for the lifeline, a line between the actor start and actor end.
   */
  private String lifeline(Actor actor) {
    final var col = new ActorColumn(actor);
    final var start = CellLocation.diagram(EntryType.Entered, col).name();
    final var end = CellLocation.diagram(EntryType.Exited, col).name();
    return "\\draw[rclifeline] (%s) -- (%s);".formatted(start, end);
  }
}
