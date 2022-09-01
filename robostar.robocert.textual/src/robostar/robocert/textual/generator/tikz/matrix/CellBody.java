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
 * The body of a cell.
 *
 * @author Matt Windsor
 */
public interface CellBody {

  /**
   * Renders style information for this cell body.
   * @param tikz TikZ generator to use for rendering the body.
   * @return TikZ code for the style (square brackets) part of a node.  If empty, there will be no styling.
   */
  Optional<String> renderStyle(TikzStructureGenerator tikz);

  /**
   * Renders the label for this cell body.
   * @param tikz TikZ generator to use for rendering the body.
   * @return TikZ code for the label (curly brackets) part of a node.  If empty, the cell will become a coordinate.
   */
  Optional<String> renderLabel(TikzStructureGenerator tikz);
}
