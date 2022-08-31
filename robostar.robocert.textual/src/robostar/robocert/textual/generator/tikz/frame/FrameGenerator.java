/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.frame;

import java.util.Optional;
import robostar.robocert.Interaction;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.Event;
import robostar.robocert.textual.generator.tikz.util.InteractionUnwinder.EventType;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Generates frames from diagram elements.
 */
public class FrameGenerator {

  /**
   * Generates frames from entries in an unrolled diagram.
   *
   * @param event event from which we are generating frames; only entry events generate frames.
   * @return an optional frame with depth information produced from the event given.
   */
  public Optional<NestedFrame> generate(Event event) {
    // Don't produce duplicate frames.
    if (event.type() == EventType.Exited) {
      return Optional.empty();
    }

    return Optional.ofNullable(new Switch().doSwitch(event.subject()))
        .map(x -> new NestedFrame(x, event.depth()));
  }

  private static class Switch extends RoboCertSwitch<Frame> {

    @Override
    public Frame caseInteraction(Interaction object) {
      return new DiagramFrame(object);
    }
  }
}
