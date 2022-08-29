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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.CombinedFragment;
import robostar.robocert.ComponentActor;
import robostar.robocert.Interaction;
import robostar.robocert.TargetActor;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.Entry;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EntryType;
import robostar.robocert.textual.generator.tikz.util.NodeNamer;
import robostar.robocert.textual.generator.tikz.util.NodeNamer.ActorColumn;
import robostar.robocert.textual.generator.tikz.util.NodeNamer.Diagram;
import robostar.robocert.textual.generator.tikz.util.NodeNamer.Edge;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Assembles an intermediate form of the TikZ diagram, ready to be turned into code.
 *
 * @author Matt Windsor
 */
public record DiagramStateBuilder(TikzStructureGenerator tikz, NodeNamer nodeNamer,
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
    return new RowSwitch(entry.type(), entry.id()).doSwitch(entry.subject());
  }

  private class RowSwitch extends RoboCertSwitch<Stream<String>> {

    private final EntryType type;
    private final int id;

    public RowSwitch(EntryType type, int id) {
      super();
      this.type = type;
      this.id = id;
    }

    @Override
    public Stream<String> caseInteraction(Interaction object) {
      final var row = new NodeNamer.Diagram(type);

      // Construct the actor nodes on the top row of the diagram.
      final var actorCells = actors.stream().map(x -> actorNode(x, row));

      final var left = tikz.coordinate(nodeNamer.node(row, Edge.Gutter));
      final var right = tikz.coordinate(nodeNamer.node(row, Edge.World));

      return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
    }

    @Override
    public Stream<String> caseCombinedFragment(CombinedFragment object) {
      final var row = new NodeNamer.CombinedFragment(type, id);

      final var actorCells = actors.stream().map(_x -> "");

      final var left = tikz.coordinate(nodeNamer.node(row, Edge.Gutter));
      final var right = tikz.coordinate(nodeNamer.node(row, Edge.World));

      return Streams.concat(Stream.of(left), actorCells, Stream.of(right));
    }
  }

  private String actorNode(Actor actor, Diagram row) {
    final var nodeName = nodeNamer.node(row, new ActorColumn(actor));
    return (row.type() == EntryType.Entered) ? actorEntryNode(actor, nodeName)
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
