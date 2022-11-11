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
import robostar.robocert.AltFragment;
import robostar.robocert.CombinedFragment;
import robostar.robocert.Interaction;
import robostar.robocert.LoopFragment;
import robostar.robocert.OptFragment;
import robostar.robocert.UntilFragment;
import robostar.robocert.XAltFragment;
import robostar.robocert.textual.generator.tikz.frame.BasicFrame.Type;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.Event;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Generates frames from diagram elements.
 */
public class FrameGenerator {

  /**
   * Generates frames from events in an unrolled diagram.
   *
   * @param event event from which we are generating frames; only entry events generate frames.
   * @return an optional frame with depth information produced from the event given.
   */
  public Optional<NestedFrame> generate(Event event) {
    // Don't produce duplicate frames.
    if (event.type() == EventType.Exited) {
      return Optional.empty();
    }

    final var id = event.id();
    return Optional.ofNullable(new RoboCertSwitch<Frame>() {
      @Override
      public Frame caseInteraction(Interaction object) {
        return new DiagramFrame(object);
      }

      @Override
      public Frame caseCombinedFragment(CombinedFragment object) {
        // Catch-all canary for if we don't support the fragment directly.
        return new MissingFrame(object, id);
      }

      @Override
      public Frame caseUntilFragment(UntilFragment object) {
        return new UntilFrame(object, id);
      }


      @Override
      public Frame caseLoopFragment(LoopFragment object) {
        return new LoopFrame(object, id);
      }

      @Override
      public Frame caseAltFragment(AltFragment object) {
        return new BasicFrame(Type.Alt, id);
      }

      @Override
      public Frame caseOptFragment(OptFragment object) {
        return new BasicFrame(Type.Opt, id);
      }

      @Override
      public Frame caseXAltFragment(XAltFragment object) {
        return new BasicFrame(Type.XAlt, id);
      }
    }.doSwitch(event.subject())).map(x -> new NestedFrame(x, event.depth()));
  }

}
