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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.inject.Inject;

import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tikz.frame.FrameGenerator;
import robostar.robocert.textual.generator.tikz.frame.NestedFrame;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.RowGenerator;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener;

/**
 * Assembles an intermediate form of the TikZ diagram, ready to be turned into code.
 *
 * @author Matt Windsor
 */
public record DiagramContentsGenerator(FrameGenerator frameGen, RowGenerator rowGen) {

  @Inject
  public DiagramContentsGenerator {
    Objects.requireNonNull(frameGen);
    Objects.requireNonNull(rowGen);
  }

  /**
   * Builds intermediate diagram state.
   *
   * @return the built state, ready to be formatted into TikZ code.
   */
  public State generate(Interaction it) {
    final var actors = it.getActors().stream().filter(x -> !(x instanceof World)).toList();

    final var unwound = new InteractionFlattener(it).unwind();

    final var matrixRows = new ArrayList<List<Cell>>();
    final var frames = new ArrayList<NestedFrame>();
    final var branchSplits = new ArrayList<BranchSplit>();

    for (var entry : unwound.events()) {
      rowGen.generate(entry, actors).ifPresent(matrixRows::add);
      frameGen.generate(entry).ifPresent(frames::add);

      if (entry.isBranchSplit()) {
        branchSplits.add(new BranchSplit(entry.id(), entry.depth()));
      }
    }
    return new State(actors, matrixRows, frames, branchSplits, unwound.maxDepth());
  }


  /**
   * Diagram state produced by this intermediate step.
   *
   * @param lifelines       actors forming lifelines in this diagram.
   * @param matrixRows      rows to place in the diagram's matrix.
   * @param frames          frames to render on top of the diagram's matrix.
   * @param branchSplits    rows on which two branches split from each other.
   * @param outerDepthScale amount by which we should scale the outer frame's margin.
   */
  public record State(List<Actor> lifelines, List<List<Cell>> matrixRows, List<NestedFrame> frames,
                      List<BranchSplit> branchSplits, int outerDepthScale) {

  }
}