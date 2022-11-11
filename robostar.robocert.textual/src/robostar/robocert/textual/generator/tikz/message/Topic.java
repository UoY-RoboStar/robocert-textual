/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.message;

import org.eclipse.emf.ecore.EObject;
import robostar.robocert.EventTopic;
import robostar.robocert.MessageTopic;
import robostar.robocert.OperationTopic;
import robostar.robocert.RoboCertPackage;
import robostar.robocert.textual.generator.tikz.util.NameSanitiser;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Represents a message topic.
 *
 * @param topic topic to be rendered into TikZ.
 *
 * @author Matt Windsor
 */
public record Topic(MessageTopic topic) implements Renderable {

  @Override
  public String render(Renderable.Context ctx) {
    final var cmdName = "rc%stopic".formatted(typeName());
    final var name = NameSanitiser.sanitise(topicName());
    return ctx.tikz().command(cmdName).argument(name).render();
  }

  private String typeName() {
    return switch(topic.eClass().getClassifierID()) {
      case RoboCertPackage.OPERATION_TOPIC -> "op";
      case RoboCertPackage.EVENT_TOPIC -> "event";
      default -> throw new IllegalArgumentException("unsupported topic: %s".formatted(topic));
    };
  }

  private String topicName() {
    return new RoboCertSwitch<String>() {
      @Override
      public String defaultCase(EObject e) {
        return e.toString();
      }

      @Override
      public String caseOperationTopic(OperationTopic o) {
        return o.getOperation().getName();
      }

      @Override
      public String caseEventTopic(EventTopic e) {
        // TODO(@MattWindsor91): I think this is duplicating something in the CSP gen.
        final var efrom = e.getEfrom();
        final var eto = e.getEto();

        // Take name from whichever event is present.
        // If both are present, and different, return eFrom/eTo.
        if (efrom == null) {
          return eto == null ? "??" : eto.getName();
        }
        if (eto == null || efrom.getName().equals(eto.getName())) {
          return efrom.getName();
        }
        return "%s/%s".formatted(efrom.getName(), eto.getName());
      }
    }.doSwitch(topic);
  }
}
