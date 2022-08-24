/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 */

package robostar.robocert.textual.generator.tikz.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.Entry;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.EntryType;

/**
 * Generates TikZ for one diagram.
 *
 * @author Matt Windsor
 */
public class DiagramGenerator {

  /**
   * Generates TikZ for a diagram.
   *
   * @param it interaction diagram to generate.
   * @return the generated TikZ code (to include within TeX).
   */
  public CharSequence generate(Interaction it) {
    // We treat the World separately -- it always appears at the end of a row.
    final var actors = it.getActors().stream().filter(x -> !(x instanceof World)).toList();

    final State state = generateState(it,
        actors);

    final var heading = """
        %% Remember to \\input or import the baseline definitions for RoboCert TikZ files.
        %% See the standalone .tex file for an example.
        """;

    final var matrix = state.matrixRows.stream()
        .collect(Collectors.joining("\n", "\\matrix[rcseq]{\n", "\n};"));

    final var targetName = String.join("::", it.getGroup().getName(),
        it.getGroup().getTarget().toString());
    final var frame = "\\rcseqframe{diagram_b_enter}{diagram_w_exit}{%s}{%s}".formatted(targetName,
        it.getName());

    return String.join("\n\n", heading, matrix, frame);
  }

  private State generateState(Interaction it, List<Actor> actors) {
    final var state = new State(new ArrayList<>());
    for (var entry : new InteractionUnwinder(it).unwind()) {
      final var row = matrixRow(actors, entry);
      if (row != null) {
        state.matrixRows.add(row);
      }
    }
    return state;
  }

  private String matrixRow(List<Actor> actors, Entry entry) {
    final var cells = matrixRowCells(actors, entry);
    return cells == null ? null : cells.collect(Collectors.joining(" & ", "  ", " \\\\"));
  }

  private Stream<String> matrixRowCells(List<Actor> actors, Entry entry) {
    final var subject = entry.subject();
    final var type = entry.type();

    if (subject instanceof Interaction) {
      return diagramBoundaryRowCells(actors, type);
    }

    return null;
  }

  private Stream<String> diagramBoundaryRowCells(List<Actor> actors, EntryType type) {
    return Streams.concat(Stream.of(diagramBoundary(false, type)), actors.stream().map(_a -> ""),
        Stream.of(diagramBoundary(true, type)));
  }

  private String diagramBoundary(boolean isWorld, EntryType type) {
    final var actor = isWorld ? "w" : "b";
    return coordinate("diagram_%s_%s".formatted(actor, type.toString()));
  }

  private String coordinate(String name) {
    return "\\coordinate(%s);".formatted(name);
  }

  private record State(List<String> matrixRows) {

  }
}
