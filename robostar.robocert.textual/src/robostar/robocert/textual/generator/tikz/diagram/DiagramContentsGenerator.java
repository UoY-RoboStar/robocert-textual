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
import java.util.Optional;
import java.util.stream.Stream;
import robostar.robocert.Interaction;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.OccurrenceFragment;
import robostar.robocert.World;
import robostar.robocert.textual.generator.tikz.frame.FrameGenerator;
import robostar.robocert.textual.generator.tikz.frame.NestedFrame;
import robostar.robocert.textual.generator.tikz.matrix.Cell;
import robostar.robocert.textual.generator.tikz.matrix.CellAlias;
import robostar.robocert.textual.generator.tikz.matrix.CombinedFragmentRow;
import robostar.robocert.textual.generator.tikz.matrix.OccurrenceRow;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.matrix.RowGenerator;
import robostar.robocert.textual.generator.tikz.message.LifelineMessage;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.Renderable;

/**
 * Assembles an intermediate form of the TikZ diagram, ready to be turned into code.
 *
 * @author Matt Windsor
 */
public class DiagramContentsGenerator {

  private List<Lifeline> lifelines = new ArrayList<>();
  private List<List<Cell>> matrixRows = new ArrayList<>();
  private final List<NestedFrame> frames = new ArrayList<>();
  private final List<BranchSplit> branchSplits = new ArrayList<>();
  private final List<LifelineMessage> messages = new ArrayList<>();
  private final List<CellAlias> aliases = new ArrayList<>();
  private int outerDepthScale = 0;

  private final FrameGenerator frameGen;
  private final RowGenerator rowGen;

  public DiagramContentsGenerator(FrameGenerator frameGen, RowGenerator rowGen) {
    this.frameGen = frameGen;
    this.rowGen = rowGen;
  }

  /**
   * Populates intermediate diagram state.
   *
   * @param it interaction from which we are populating the state.
   */
  public void generate(Interaction it) {
    lifelines = it.getActors().stream().filter(x -> !(x instanceof World))
        .map(Lifeline::new).toList();

    final var unwound = new InteractionFlattener(it).unwind();
    outerDepthScale = unwound.maxDepth();

    for (var entry : unwound.events()) {
      // TODO(@MattWindsor91): normalise these generators, possibly.
      rowGen.generate(entry, lifelines).ifPresent(matrixRows::add);
      frameGen.generate(entry).ifPresent(frames::add);

      if (entry.isBranchSplit()) {
        branchSplits.add(new BranchSplit(entry.id(), entry.depth()));
      }
      if (entry.subject() instanceof OccurrenceFragment o) {
        if (o.getOccurrence() instanceof MessageOccurrence m) {
          messages.add(new LifelineMessage(m, entry.id()));
        }
      }
    }

    flattenRows();
  }

  /**
   * Gets all of the generated contents as a stream of streams of renderables.
   *
   * @return the renderable contents (less, for now, the matrix).
   */
  public Stream<Stream<Renderable>> contents() {
    // the x -> x is used for type upcasting
    return Stream.of(aliases.stream().map(x -> x), lifelines.stream().map(x -> x), frames.stream().map(x -> x),
        branchSplits.stream().map(x -> x), messages.stream().map(x -> x));
  }

  private void flattenRows() {
    final var newRows = new ArrayList<List<Cell>>(matrixRows.size());

    // TODO(@MattWindsor91): clean this up!!
    List<Cell> lastRlist = null;
    Optional<Row> lastRow = Optional.empty();
    for (var rlist : matrixRows) {
      final var row = extractRow(rlist);
      var skipped = false;

      if (lastRow.isPresent()) {
        if (row.isPresent()) {
          if (shouldMerge(lastRow.get(), row.get())) {
            for (var i = 0; i < rlist.size(); i++) {
              aliases.add(new CellAlias(lastRlist.get(i), rlist.get(i)));
            }
            skipped = true;
          }
        }
      }

      if (!skipped) {
        newRows.add(rlist);
      }
      lastRlist = rlist;
      lastRow = row;
    }

    matrixRows = newRows;
  }

  private static boolean shouldMerge(Row last, Row current) {
    var lastIsCfEntry = false;
    var lastIsCfExit = false;
    var currIsCfExit = false;

    if (last instanceof CombinedFragmentRow lrc) {
      lastIsCfEntry = lrc.type() == EventType.Entered;
      lastIsCfExit = lrc.type() == EventType.Exited;
    }

    if (current instanceof CombinedFragmentRow crc) {
      currIsCfExit = crc.type() == EventType.Exited;
    }
    final var currIsOf = current instanceof OccurrenceRow;

    final var isEntryMerger = lastIsCfEntry && currIsOf;
    final var isDoubleExit = lastIsCfExit && currIsCfExit;

    return isEntryMerger || isDoubleExit;
  }

  private Optional<Row> extractRow(List<Cell> rlist) {
    if (rlist.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(rlist.get(0).row());
  }

  /**
   * @return the outer depth scale.
   */
  public int outerDepthScale() {
    return outerDepthScale;
  }

  /**
   * @return the matrix rows.
   */
  public List<List<Cell>> matrixRows() {
    return matrixRows;
  }
}
