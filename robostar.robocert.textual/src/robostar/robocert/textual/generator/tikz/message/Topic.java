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

import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.MessageTopic;
import robostar.robocert.RoboCertPackage;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Represents a message topic.
 *
 * @param topic topic to be rendered into TikZ.
 *
 * @author Matt Windsor
 */
public record Topic(MessageTopic topic) implements Renderable {

  @Override
  public String render(TikzStructureGenerator tikz, ISerializer ser) {
    final var cmdName = "rc%stopic".formatted(typeName());
    return tikz.command(cmdName).render();
  }

  private String typeName() {
    return switch(topic.eClass().getClassifierID()) {
      case RoboCertPackage.OPERATION_TOPIC -> "op";
      case RoboCertPackage.EVENT_TOPIC -> "event";
      default -> throw new IllegalArgumentException("unsupported topic: %s".formatted(topic));
    };
  }
}
