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

import circus.robocalc.robochart.NamedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.Interaction;
import robostar.robocert.TargetActor;
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

    final State state = generateState(it, actors);

    final var heading = """
        %% Remember to \\input or import the baseline definitions for RoboCert TikZ files.
        %% See the standalone .tex file for an example.
        """;

    final var matrix = state.matrixRows.stream()
        .collect(Collectors.joining("\n", "\\matrix[rcseq]{\n", "\n};"));

    final var lifelines = actors.stream().map(DiagramGenerator::lifeline).collect(Collectors.joining("\n"));

    final var targetName = String.join("::", it.getGroup().getName(),
        it.getGroup().getTarget().toString());
    final var frame = "\\rcseqframe{diagram_b_enter}{diagram_w_exit}{%s}{%s}".formatted(targetName,
        it.getName());

    return String.join("\n\n", heading, matrix, frame, lifelines);
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
    // Construct the actor nodes on the top row of the diagram.
    final var actorCells = actors.stream().map(x -> actorNode(x, type));

    return Streams.concat(Stream.of(diagramBoundary(false, type)), actorCells,
        Stream.of(diagramBoundary(true, type)));
  }

  private String actorNode(Actor actor, EntryType type) {
    final var nodeName = actorNodeName(actor, type);
    if (type == EntryType.Entered) {
      return actorEntryNode(actor, nodeName);
    }
    return coordinate(nodeName);
  }

  /**
   * Constructs the TikZ command for drawing an actor's lifeline.
   *
   * @param actor actor for which we are drawing a lifeline.
   * @return TikZ for the lifeline, a line between the actor start and actor end.
   */
  private static String lifeline(Actor actor) {
    final var start = actorNodeName(actor, EntryType.Entered);
    final var end = actorNodeName(actor, EntryType.Exited);
    return "\\draw[rclifeline] (%s) -- (%s);".formatted(start, end);
  }

  private static String actorNodeName(Actor actor, EntryType type) {
    return "actor_n%s_%s".formatted(actor.getName(), type.toString());
  }

  private String actorEntryNode(Actor actor, String nodeName) {
    final var text = "%s %s".formatted(actorStereotype(actor), actor.getName());
    return node("rcactor", nodeName, text);
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

  private String diagramBoundary(boolean isWorld, EntryType type) {
    final var actor = isWorld ? "w" : "b";
    return coordinate("diagram_%s_%s".formatted(actor, type.toString()));
  }

  private String node(String style, String name, String content) {
    return "\\node[%s](%s){%s};".formatted(style, name, content);
  }

  private String coordinate(String name) {
    return "\\coordinate(%s);".formatted(name);
  }

  private record State(List<String> matrixRows) {

  }
}
