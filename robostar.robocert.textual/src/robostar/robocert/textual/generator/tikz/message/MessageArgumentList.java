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

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.xtext.serializer.ISerializer;
import robostar.robocert.ValueSpecification;
import robostar.robocert.textual.generator.tikz.util.Renderable;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;

/**
 * Represents a list of messages inside a message set.
 *
 * @param args arguments to be rendered into TikZ.
 * @author Matt Windsor
 */
public record MessageArgumentList(List<ValueSpecification> args) implements Renderable {

  @Override
  public String render(TikzStructureGenerator tikz, ISerializer ser) {
    return args.stream().map(ser::serialize).collect(Collectors.joining(", "));
  }
}
