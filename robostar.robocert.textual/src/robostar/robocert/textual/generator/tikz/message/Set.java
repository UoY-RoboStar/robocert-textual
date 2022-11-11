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

import java.util.stream.Collectors;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.BinaryMessageSet;
import robostar.robocert.BinarySetOperator;
import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.MessageSet;
import robostar.robocert.RefMessageSet;
import robostar.robocert.UniverseMessageSet;
import robostar.robocert.textual.generator.tikz.util.Command;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.util.RoboCertSwitch;

public record Set(MessageSet set) implements Renderable {

  @Override
  public String render(TikzStructureGenerator tikz, ISerializer ser) {
    return new RoboCertSwitch<Command>() {
      @Override
      public Command defaultCase(EObject e) {
        throw new IllegalArgumentException("unsupported message set: %s".formatted(e));
      }

      @Override
      public Command caseBinaryMessageSet(BinaryMessageSet b) {
        // TODO(@MattWindsor91): this nesting is not great.
        final var lhs = new Set(b.getLhs()).render(tikz, ser);
        final var rhs = new Set(b.getRhs()).render(tikz, ser);
        return tikz.command("rcset%s".formatted(operatorName(b.getOperator()))).argument(lhs)
            .argument(rhs);
      }

      @Override
      public Command caseExtensionalMessageSet(ExtensionalMessageSet e) {
        final var body = e.getMessages().stream().map(m -> new SetMessage(m).render(tikz, ser))
            .collect(Collectors.joining(", "));
        return tikz.command("rcextset").argument(body);
      }

      @Override
      public Command caseUniverseMessageSet(UniverseMessageSet u) {
        return tikz.command("rcuniverseset");
      }

      @Override
      public Command caseRefMessageSet(RefMessageSet r) {
        return tikz.command("rcrefset").argument(r.getSet().getName());
      }
    }.doSwitch(set).render();
  }

  private String operatorName(BinarySetOperator operator) {
    return switch (operator) {
      case DIFFERENCE -> "diff";
      case UNION -> "union";
      case INTERSECTION -> "intersect";
    };
  }
}
