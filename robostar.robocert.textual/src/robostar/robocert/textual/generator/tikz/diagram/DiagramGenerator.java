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
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.ActorColumn;
import robostar.robocert.textual.generator.tikz.matrix.DiagramRow;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Generates TikZ for one diagram.
 *
 * @param tikz        TikZ structure generator.
 * @param contentsGen diagram contents generator.
 * @author Matt Windsor
 */
public record DiagramGenerator(TikzStructureGenerator tikz, DiagramContentsGenerator contentsGen) {

  @Inject
  public DiagramGenerator {
    Objects.requireNonNull(tikz);
    Objects.requireNonNull(contentsGen);
  }

  public static final String HEADING = """
      % Remember to \\input or import the baseline definitions for RoboCert TikZ files.
      % See the standalone .tex file for an example.
      """;


  /**
   * Generates TikZ for a diagram.
   *
   * @param it interaction diagram to generate.
   * @return the generated TikZ code (to include within TeX).
   */
  public CharSequence generate(Interaction it) {
    // We treat the World separately -- it always appears at the end of a row.

    final var state = contentsGen.generate(it);

    final var matrixStyle = "rcseq, row sep=(\\the\\rctopmargin + (%d*\\the\\rcstepmargin))".formatted(
        state.outerDepthScale());
    final var matrixPrefix = "\\matrix[%s]{\n".formatted(matrixStyle);

    final var matrix = state.matrixRows().stream().map(this::generateMatrixRow)
        .collect(Collectors.joining("\n", matrixPrefix, "\n};"));

    final var lifelines = state.lifelines().stream().map(this::lifeline)
        .collect(Collectors.joining("\n"));

    final var branchSplits = state.branchSplits().stream()
        .map(x -> x.render(tikz, state.outerDepthScale())).collect(Collectors.joining("\n"));

    final var frames = state.frames().stream().map(x -> x.render(tikz, state.outerDepthScale()))
        .collect(Collectors.joining("\n"));

    return String.join("\n\n", HEADING, matrix, lifelines, branchSplits, frames);
  }

  private String generateMatrixRow(List<Cell> row) {
    return row.stream().map(x -> x.render(tikz).orElse(""))
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
    final var start = Cell.nameOf(new DiagramRow(EventType.Entered), col);
    final var end = Cell.nameOf(new DiagramRow(EventType.Exited), col);
    return tikz.draw("rclifeline").to(start).to(end).render();
  }
}
