/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.matrix;

import circus.robocalc.robochart.NamedElement;
import java.util.Optional;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.TargetActor;
import robostar.robocert.textual.generator.tikz.util.Command;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Matrix cell body capturing the head of an actor lifeline.
 *
 * @param actor actor being captured.
 * @author Matt Windsor
 */
public record ActorHeadCellBody(Actor actor) implements CellBody {

  @Override
  public Optional<String> renderStyle(TikzStructureGenerator tikz) {
    return Optional.of("rcactor");
  }

  @Override
  public Optional<String> renderLabel(TikzStructureGenerator tikz) {
    return Optional.of("%s~%s".formatted(actorStereotype(tikz, actor).render(), actor.getName()));
  }

  private static Command actorStereotype(TikzStructureGenerator tikz, Actor actor) {
    if (actor instanceof TargetActor) {
      return tikz.command("rctarget");
    } else if (actor instanceof ComponentActor c) {
      final var cnode = c.getNode();
      final var cname = cnode instanceof NamedElement n ? n.getName() : cnode.toString();
      return tikz.command("rccomponent").argument(cname);
    }
    return tikz.command("textbf").argument("(unknown)");
  }
}
