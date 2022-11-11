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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.stream.Stream;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Inject;

import robostar.robocert.Interaction;
import robostar.robocert.textual.generator.tikz.diagram.DiagramContentsGenerator.State;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Generates TikZ for one diagram.
 *
 * @param tikz        TikZ structure generator.
 * @param ser         Xtext serializer (used for expression languages).
 * @param contentsGen diagram contents generator.
 * @author Matt Windsor
 */
public record DiagramGenerator(TikzStructureGenerator tikz, ISerializer ser,
                               DiagramContentsGenerator contentsGen) {

  @Inject
  public DiagramGenerator {
    Objects.requireNonNull(tikz);
    Objects.requireNonNull(ser);
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

    final String matrix = renderMatrix(state);

    final var contents = state.contents()
        .map(grp -> renderContentGroup(grp, state.outerDepthScale()))
        .collect(Collectors.joining("\n\n"));

    return String.join("\n\n", HEADING, matrix, contents);
  }

  private String renderContentGroup(Stream<Renderable> grp, int topLevel) {
    final var ctx = new Renderable.Context(tikz, ser, topLevel);
    return grp.map(s -> s.render(ctx)).collect(Collectors.joining("\n"));
  }

  private String renderMatrix(State state) {
    final var style = "rcsequence, row sep=(\\the\\rctopmargin + (%d*\\the\\rcstepmargin))".formatted(
        state.outerDepthScale());
    final var prefix = "\\matrix[%s]{\n".formatted(style);

    return state.matrixRows().stream().map(this::renderMatrixRow)
        .collect(Collectors.joining("\n", prefix, "\n};"));
  }

  private String renderMatrixRow(List<Cell> row) {
    return row.stream().map(x -> x.render(tikz).orElse(""))
        .collect(Collectors.joining(" & ", "  ", " \\\\"));
  }
}
