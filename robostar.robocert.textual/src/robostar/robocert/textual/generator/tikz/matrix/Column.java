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
 * Interface of things that represent columns in a TikZ sequence diagram matrix.
 *
 * @author Matt Windsor
 * @see Row
 * @see CellLocation
 */
public interface Column {

  /**
   * Gets the name stub that this column should append into the node name.
   *
   * @return generated name.
   */
  String columnName();

  /**
   * Gets the actor associated with this column if any.
   *
   * @return optional actor.
   */
  Optional<Actor> getActor();
}
