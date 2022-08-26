/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/

package robostar.robocert.textual.generator.tikz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import robostar.robocert.BlockFragment;
import robostar.robocert.BranchFragment;
import robostar.robocert.Interaction;
import robostar.robocert.InteractionOperand;
import robostar.robocert.OccurrenceFragment;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Traverses an interaction, flattening its structure into a single linear stream of events and
 * recording information about depth.
 * <p>
 * This is mostly useful for things like the TikZ generator, but is designed for generality.
 *
 * @author Matt Windsor
 */
public class InteractionUnwinder {

  private final Interaction subject;
  private int currentDepth = 0;
  private int maxDepth = 0;

  private List<Entry> entries = null;

  public InteractionUnwinder(Interaction subject) {
    this.subject = subject;
  }

  public Result unwind() {
    entries = new ArrayList<>();
    maxDepth = currentDepth = 0;
    new Switch().doSwitch(subject);
    return new Result(entries, maxDepth);
  }

  private class Switch extends RoboCertSwitch<Boolean> {

    @Override
    public Boolean caseInteraction(Interaction object) {
      add(object, EntryType.Entered);
      for (var frag : object.getFragments()) {
        doSwitch(frag);
      }
      add(object, EntryType.Exited);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseBranchFragment(BranchFragment object) {
      add(object, EntryType.Entered);

      for (var branch : object.getBranches()) {
        doSwitch(branch);
      }

      add(object, EntryType.Exited);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseBlockFragment(BlockFragment object) {
      enter(object);
      doSwitch(object.getBody());
      exit(object);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseInteractionOperand(InteractionOperand object) {
      enter(object);
      for (var inner : object.getFragments()) {
        doSwitch(inner);
      }
      exit(object);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseOccurrenceFragment(OccurrenceFragment object) {
      add(object, EntryType.Happened);

      return Boolean.TRUE;
    }
  }

  private void enter(EObject object) {
    add(object, EntryType.Entered);
    currentDepth++;
    maxDepth = Integer.max(currentDepth, maxDepth);
  }

  private void exit(EObject object) {
    currentDepth--;
    add(object, EntryType.Exited);
  }

  private void add(EObject object, EntryType etype) {
    entries.add(new Entry(etype, entries.size(), currentDepth, object));
  }

  /**
   * The result of an interaction unwinding.
   *
   * @param entries  list of all entries in occurrence order.
   * @param maxDepth maximum nesting depth (0 = top level only).
   */
  public record Result(List<Entry> entries, int maxDepth) {

  }

  public record Entry(EntryType type, int position, int depth, EObject subject) {

  }

  public enum EntryType {
    /**
     * Entered an interaction fragment.
     */
    Entered,
    /**
     * Exited an interaction fragment.
     */
    Exited,
    /**
     * An occurrence fragment occurred.
     */
    Happened,
    ;

    @Override
    public String toString() {
      return switch (this) {
        case Exited -> "exit";
        case Entered -> "enter";
        case Happened -> "occ";
      };
    }
  }
}
