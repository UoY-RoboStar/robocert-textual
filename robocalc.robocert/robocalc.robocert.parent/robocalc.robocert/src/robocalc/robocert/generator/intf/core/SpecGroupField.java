/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.intf.core;

/**
 * Enumeration of fields in a specification group.
 * <p>
 * Specification groups that contain targets have a regular structure, where the target's
 * parametrisation is exposed in both 'closed' and 'open' forms, along with its tick-tock context.
 * <p>
 * These fields currently correspond directly to tock-CSP subdefinitions, but this may eventually
 * change.
 *
 * @author Matt Windsor
 */
public enum SpecGroupField {
  //
  // Common to all groups
  //

  /**
   * The closed form of the parametric part of a group definition.
   */
  PARAMETRIC_CLOSED,
  /**
   * The open form of the parametric part of a group definition.
   */
  PARAMETRIC_OPEN,
  /**
   * The module instance in the group providing its context for tick-tock module shifting.
   */
  TICK_TOCK_CONTEXT,
  /**
   * The set of all events on which the target can participate.
   */
  UNIVERSE,

  //
  // Sequences
  //

  /**
   * The module in the specification group containing any control channel definitions.
   */
  CHANNEL_MODULE,

  /**
   * The enumeration of actors in the specification group.
   */
  ACTOR_ENUM,

  /**
   * The module in a sequence group containing any named message sets.
   */
  MESSAGE_SET_MODULE;

  @Override
  public String toString() {
    return switch (this) {
      case CHANNEL_MODULE -> "Channels";
      case ACTOR_ENUM -> "Actors";
      case MESSAGE_SET_MODULE -> "MsgSets";
      case PARAMETRIC_CLOSED -> "Closed";
      case PARAMETRIC_OPEN -> "Open";
      case TICK_TOCK_CONTEXT -> "TTContext";
      case UNIVERSE -> "Universe";
    };
  }
}