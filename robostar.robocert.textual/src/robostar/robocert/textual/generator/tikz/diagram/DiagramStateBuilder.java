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
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.Interaction;
import robostar.robocert.TargetActor;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.Entry;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.EntryType;
import robostar.robocert.textual.generator.tikz.TikzNodeNamer;
import robostar.robocert.textual.generator.tikz.TikzStructureGenerator;

/**
 * Assembles an intermediate form of the TikZ diagram, ready to be turned into code.
 *
 * @author Matt Windsor
 */
public record DiagramStateBuilder(TikzStructureGenerator tikz, TikzNodeNamer nodeNamer,
                                  Interaction it, List<Actor> actors) {
  // TODO: decouple this more from the formatting of the code.

  /**
   * Builds intermediate diagram state.
   *
   * @return the built state, ready to be formatted into TikZ code.
   */
  public State build() {
    final var unwound = new InteractionUnwinder(it).unwind();

    final var matrixRows = new ArrayList<String>();

    for (var entry : unwound.entries()) {
      final var row = matrixRow(entry);
      if (row != null) {
        matrixRows.add(row);
      }
    }
    return new State(List.copyOf(matrixRows), unwound.maxDepth());
  }

  private String matrixRow(Entry entry) {
    final var cells = matrixRowCells(entry);
    return cells == null ? null : cells.collect(Collectors.joining(" & ", "  ", " \\\\"));
  }

  private Stream<String> matrixRowCells(Entry entry) {
    final var subject = entry.subject();
    final var type = entry.type();

    if (subject instanceof Interaction) {
      return diagramBoundaryRowCells(type);
    }

    return null;
  }

  private Stream<String> diagramBoundaryRowCells(EntryType type) {
    // Construct the actor nodes on the top row of the diagram.
    final var actorCells = actors.stream().map(x -> actorNode(x, type));

    final var left = tikz.coordinate(nodeNamer.diagramCorner(false, type));
    final var right = tikz.coordinate(nodeNamer.diagramCorner(true, type));
    return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
  }

  private String actorNode(Actor actor, EntryType type) {
    final var nodeName = nodeNamer.actor(actor, type);
    return (type == EntryType.Entered) ? actorEntryNode(actor, nodeName)
        : tikz.coordinate(nodeName);
  }


  private String actorEntryNode(Actor actor, String nodeName) {
    final var text = "%s %s".formatted(actorStereotype(actor), actor.getName());
    return tikz.node("rcactor", nodeName, text);
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
   * @param outerDepthScale amount by which we should scale the outer frame's margin.
   */
  public record State(List<String> matrixRows, int outerDepthScale) {

  }
}
