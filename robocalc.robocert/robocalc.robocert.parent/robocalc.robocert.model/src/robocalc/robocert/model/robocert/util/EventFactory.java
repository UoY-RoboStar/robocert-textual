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
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import com.google.inject.Inject;
import java.util.Objects;

/**
 * A high-level factory for RoboChart events.
 *
 * @param rc the underlying RoboChart factory.
 * @author Matt Windsor
 */
public record EventFactory(RoboChartFactory rc) {

  /**
   * Constructs an event factory.
   *
   * @param rc the underlying RoboChart factory.
   */
  @Inject
  public EventFactory {
    Objects.requireNonNull(rc);
  }

  /**
   * Creates an untyped, unicast event.
   *
   * @param name the name of the event.
   * @return the event.
   */
  public Event event(String name) {
    final var it = rc.createEvent();
    it.setName(name);
    return it;
  }

  /**
   * Creates an typed, unicast event.
   *
   * @param name the name of the event.
   * @param ty   the type of the event.
   * @return the event.
   */
  public Event event(String name, Type ty) {
    final var it = event(name);
    it.setType(ty);
    return it;
  }
}
