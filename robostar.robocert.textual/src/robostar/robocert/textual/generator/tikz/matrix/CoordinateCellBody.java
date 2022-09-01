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
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Dummy cell body that emits neither a style nor a body for the cell.
 *
 * @author Matt Windsor
 */
public class CoordinateCellBody implements CellBody {

  @Override
  public Optional<String> renderStyle(TikzStructureGenerator tikz) {
    return Optional.empty();
  }

  @Override
  public Optional<String> renderLabel(TikzStructureGenerator tikz) {
    return Optional.empty();
  }
}
