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

import java.util.Objects;

import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.Interaction;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;
import robostar.robocert.textual.generator.tikz.matrix.DiagramRow;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.NameSanitiser;
import robostar.robocert.textual.generator.tikz.util.TargetTypeNameGenerator;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.util.resolve.TargetElementResolver;

/**
 * A frame that represents the outer layer of a diagram.
 *
 * @param diagram diagram being represented by this frame.
 */
public record DiagramFrame(Interaction diagram) implements Frame {

  @Override
  public Row row(EventType type) {
    return new DiagramRow(type);
  }

  @Override
  public String generateLabel(TikzStructureGenerator tikz, ISerializer _ser) {
    final var group = diagram.getGroup();
    final var target = group == null ? null : group.getTarget();

    // TODO(@MattWindsor91): it seems strange to construct this here.
    final var targetType = new TargetTypeNameGenerator(tikz).targetTypeName(target);
    final var targetName = Objects.requireNonNullElse(targetName(group, target), "???");

    final var diagramName = NameSanitiser.sanitise(diagram.getName());

    return tikz.command("rcsequence").argument(diagramName).argument(targetType)
        .argument(targetName).render();
  }

  private static String targetName(SpecificationGroup group, Target target) {
    if (group == null || target == null) {
      return null;
    }
    // TODO(@MattWindsor91): it seems strange to construct this here.
    final var elem = new TargetElementResolver().resolve(target);
    if (elem == null) {
      return null;
    }
    final var baseName = elem.getName();
    if (baseName == null) {
      return null;
    }
    return NameSanitiser.sanitise(String.join("::", group.getName(), baseName));
  }
}
