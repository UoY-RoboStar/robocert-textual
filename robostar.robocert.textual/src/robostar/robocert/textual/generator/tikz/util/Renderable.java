/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.util;

import org.eclipse.xtext.serializer.ISerializer;

/**
 * Interface representing a sequence diagram element that can be rendered to TikZ.
 *
 * @author Matt Windsor
 */
public interface Renderable {

  /**
   * Renders the element.
   *
   * @param tikz TikZ structure generator, used to create TikZ commands.
   * @param ser  serialiser used for rendering expressions.
   * @return string of rendered TikZ.
   */
  String render(TikzStructureGenerator tikz, ISerializer ser);
}
