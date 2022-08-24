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
import robostar.robocert.InteractionFragment;
import robostar.robocert.InteractionOperand;
import robostar.robocert.OccurrenceFragment;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Traverses an interaction, flattening its events.
 */
public class InteractionUnwinder {
  private final Interaction subject;
  private List<Entry> entries = null;

  public InteractionUnwinder(Interaction subject) {
    this.subject = subject;
  }

  public List<Entry> unwind() {
    entries = new ArrayList<>();
    new Switch().doSwitch(subject);
    return entries;
  }

  private class Switch extends RoboCertSwitch<Boolean> {
    @Override
    public Boolean caseInteraction(Interaction object) {
      add(object, EntryType.Entered);
      for (var frag: object.getFragments()) {
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
      add(object, EntryType.Entered);
      doSwitch(object.getBody());
      add(object, EntryType.Exited);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseInteractionOperand(InteractionOperand object) {
      add(object, EntryType.Entered);
      for (var inner : object.getFragments()) {
        doSwitch(inner);
      }
      add(object, EntryType.Exited);

      return Boolean.TRUE;
    }

    @Override
    public Boolean caseOccurrenceFragment(OccurrenceFragment object) {
      add(object, EntryType.Happened);

      return Boolean.TRUE;
    }
  }

  private boolean add(EObject object, EntryType etype) {
    return entries.add(new Entry(etype, entries.size(), object));
  }

  public record Entry(EntryType type, int position, EObject subject) {}

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
