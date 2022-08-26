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

package robostar.robocert.textual.generator.tikz;

import robostar.robocert.Actor;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.EntryType;

/**
 * Handles the naming conventions used in RoboCert TikZ diagrams for nodes.
 * <p>
 * Connections between nodes, as well as relative positioning thereof, requires common conventions
 * for node naming, which this class enforces.
 *
 * @author Matt Windsor
 */
public class TikzNodeNamer {

  /**
   * Constructs a node name for an actor.
   *
   * @param actor actor whose node name is desired.
   * @param type  type of use of this actor (entry or exit).
   * @return constructed name of the node.
   */
  public String actor(Actor actor, EntryType type) {
    assert (type == EntryType.Entered || type == EntryType.Exited);
    return "actor_n%s_%s".formatted(actor.getName(), type.toString());
  }


  /**
   * Constructs a coordinate name for one of the four corners of the diagram.
   *
   * @param isWorld whether the corner is on the world (right-hand) side of the diagram.
   * @param type whether the corner is entry (top) or exit (bottom).
   * @return constructed name of the coordinate.
   */
  public String diagramCorner(boolean isWorld, EntryType type) {
    assert (type == EntryType.Entered || type == EntryType.Exited);
    return "diagram_%s_%s".formatted(isWorld ? "w" : "b", type.toString());
  }
}
