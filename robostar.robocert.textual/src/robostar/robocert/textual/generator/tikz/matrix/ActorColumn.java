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

import java.util.Optional;
import robostar.robocert.Actor;

/**
 * Encapsulates an {@link Actor} in a {@link Column}.
 *
 * @param actor actor to be wrapped.
 */
public record ActorColumn(Actor actor) implements Column {

  @Override
  public String columnName() {
    return "a" + actor.getName();
  }

  @Override
  public Optional<Actor> getActor() {
    return Optional.of(actor);
  }
}
