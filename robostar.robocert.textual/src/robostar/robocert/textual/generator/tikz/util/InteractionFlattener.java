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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
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
public class InteractionFlattener {

  private final Interaction subject;
  private int maxDepth = 0;
  private int currentId = 0;

  private int currentRelativeId = 0;
  private final Deque<Integer> relativeIdStack = new ArrayDeque<>();
  private final Map<Integer, Integer> relativeIdMap = new HashMap<>();

  private int getCurrentDepth() {
    return relativeIdStack.size();
  }

  private List<Event> entries = null;

  public InteractionFlattener(Interaction subject) {
    this.subject = subject;
  }

  public Result unwind() {
    entries = new ArrayList<>();
    maxDepth = 0;
    new Switch().doSwitch(subject);
    return new Result(entries, maxDepth);
  }

  private class Switch extends RoboCertSwitch<Boolean> {

    @Override
    public Boolean caseInteraction(Interaction object) {
      final var id = enterNested(object);
      for (var frag : object.getFragments()) {
        doSwitch(frag);
      }
      exitNested(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseBranchFragment(BranchFragment object) {
      final var id = enterNested(object);
      for (var branch : object.getBranches()) {
        doSwitch(branch);
      }
      exitNested(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseBlockFragment(BlockFragment object) {
      final var id = enterNested(object);
      doSwitch(object.getBody());
      exitNested(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseInteractionOperand(InteractionOperand object) {
      // Don't create a separate nesting level for interaction operands.
      // If we ever need to make it possible to distinguish these by nesting level, we'll have to
      // make it a variable of the flattener, I think.
      final var id = enter(object);
      for (var inner : object.getFragments()) {
        doSwitch(inner);
      }
      exit(object, id);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseOccurrenceFragment(OccurrenceFragment object) {
      enter(object);
      return Boolean.TRUE;
    }
  }

  /**
   * Marks entry of a nested object (combined fragment or diagram), increasing the depth level.
   *
   * @param object object to enter
   * @return ID assigned to the object (to use with exitNested).
   */
  private int enterNested(EObject object) {
    final int id = enter(object);

    relativeIdStack.push(currentRelativeId);
    currentRelativeId = 0;

    maxDepth = Integer.max(getCurrentDepth(), maxDepth);

    return id;
  }

  /**
   * Marks entry of an object without increasing the depth level.
   *
   * @param object object to enter
   * @return ID assigned to the object (to use with exit).
   */
  private int enter(EObject object) {
    final var id = currentId;
    currentId++;

    final var rid = currentRelativeId;
    currentRelativeId++;

    relativeIdMap.put(id, rid);

    add(object, id, EventType.Entered);
    return id;
  }

  /**
   * Marks exit of a nested object, decreasing the depth level.
   *
   * @param object object to exit.
   * @param id     ID assigned to the object by enter.
   */
  private void exitNested(EObject object, int id) {
    final var currentDepth = getCurrentDepth();
    assert (0 < currentDepth);
    assert (currentDepth <= maxDepth);

    currentRelativeId = relativeIdStack.pop();

    exit(object, id);
  }

  /**
   * Marks exit of an object, assigning the exit event the given ID.
   *
   * @param object object to exit.
   * @param id     ID assigned to the object by enter.
   */
  private void exit(EObject object, int id) {
    assert (id < currentId);
    add(object, id, EventType.Exited);
  }

  private void add(EObject object, int id, EventType etype) {
    entries.add(new Event(etype, id, relativeIdMap.get(id), getCurrentDepth(), object));
  }

  /**
   * The result of an interaction flattening.
   *
   * @param events   list of all events in occurrence order.
   * @param maxDepth maximum nesting depth (0 = top level only).
   */
  public record Result(List<Event> events, int maxDepth) {

  }

  /**
   * An element of a flattened interaction.
   *
   * @param type       whether we are entering or exiting the element.
   * @param id         global sequential ID of the element.
   * @param relativeId relative ID within this element's parent.
   * @param depth      depth of the element within the interaction.
   * @param subject    element body.
   */
  public record Event(EventType type, int id, int relativeId, int depth, EObject subject) {

    /**
     * Is this a split between branches?
     * @return true if this event is an interaction operand, is not the first event inside its parent, and is an entry event.
     */
    public boolean isBranchSplit() {
      return subject instanceof InteractionOperand && type == EventType.Entered && 0 < relativeId;
    }
  }

  /**
   * Types of event in a flattened interaction.
   *
   * @author Matt Windsor
   */
  public enum EventType {
    /**
     * Entered an interaction fragment, or visited an occurrence fragment.
     */
    Entered,
    /**
     * Exited an interaction fragment (this is never emitted for occurrence fragments).
     */
    Exited;

    @Override
    public String toString() {
      return switch (this) {
        case Exited -> "exit";
        case Entered -> "enter";
      };
    }
  }
}
