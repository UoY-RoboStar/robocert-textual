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

import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.textual.generator.tikz.matrix.Row;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Interface of types of frame in a TikZ encoding of a sequence diagram.
 * <p>
 * Frames typically correspond to the outer rim of a combined fragment, but can also represent
 * diagrams.
 *
 * @author Matt Windsor
 */
public interface Frame {

  /**
   * Gets the matrix row for this frame given that we are either entering or exiting the frame.
   *
   * @param type whether we are entering or exiting the frame.
   * @return a matrix row corresponding to the entry or exit of this frame.
   */
  Row row(EventType type);

  /**
   * Generates the label for the top corner of this frame.
   *
   * @param tikz TikZ structure generator (eg, for generating commands).
   * @param ser Xtext serializer (eg, for rendering expressions).
   * @return string of TikZ code for the top corner, to be spliced into the frame macro.
   */
  String generateLabel(TikzStructureGenerator tikz, ISerializer ser);
}
