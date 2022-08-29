/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.util;

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
 * recording information about depth, while also giving each item a unique sequential ID.
 * <p>
 * This is mostly useful for things like the TikZ generator, but is designed for generality.
 *
 * @author Matt Windsor
 */
public class InteractionUnwinder {

  private final Interaction subject;
  private int currentDepth = 0;
  private int maxDepth = 0;
  private int currentId = 0;

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
      final var id = enter(object);
      for (var frag : object.getFragments()) {
        doSwitch(frag);
      }
      exit(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseBranchFragment(BranchFragment object) {
      final var id = enter(object);
      for (var branch : object.getBranches()) {
        doSwitch(branch);
      }
      exit(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseBlockFragment(BlockFragment object) {
      final var id = enter(object);
      doSwitch(object.getBody());
      exit(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseInteractionOperand(InteractionOperand object) {
      final var id = enter(object);
      for (var inner : object.getFragments()) {
        doSwitch(inner);
      }
      exit(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseOccurrenceFragment(OccurrenceFragment object) {
      add(object, currentId, EntryType.Happened);
      currentId++;

      return Boolean.TRUE;
    }
  }

  /**
   * Marks entry of a compound object.
   *
   * @param object object to enter
   * @return ID assigned to the object (to use with exit).
   */
  private int enter(EObject object) {
    final var id = currentId;
    currentId++;

    add(object, id, EntryType.Entered);

    currentDepth++;
    maxDepth = Integer.max(currentDepth, maxDepth);

    return id;
  }

  /**
   * Marks exit of a compound object.
   *
   * @param object object to exit.
   * @param id ID assigned to the object by enter.
   */
  private void exit(EObject object, int id) {
    assert(0 < currentDepth);
    assert(currentDepth <= maxDepth);
    assert(id < currentId);

    currentDepth--;
    add(object, id, EntryType.Exited);
  }

  private void add(EObject object, int id, EntryType etype) {
    entries.add(new Entry(etype, id, currentDepth, object));
  }

  /**
   * The result of an interaction unwinding.
   *
   * @param entries  list of all entries in occurrence order.
   * @param maxDepth maximum nesting depth (0 = top level only).
   */
  public record Result(List<Entry> entries, int maxDepth) {

  }

  public record Entry(EntryType type, int id, int depth, EObject subject) {

  }

  /**
   * Types of entry in an unwound interaction.
   *
   * @author Matt Windsor
   */
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
