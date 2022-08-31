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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder for TikZ/LaTeX commands.
 *
 * @author Matt Windsor
 */
public class Command {

  /**
   * List of arguments to append to the command.
   */
  private List<Argument> arguments = new ArrayList<>();

  /**
   * Whether this is a TikZ node.
   */
  private boolean isNode;

  /**
   * Name of the command.
   */
  private String name;

  /**
   * Constructs a command builder with a command name.
   *
   * @param name name of the command.
   */
  public Command(String name) {
    Objects.requireNonNull(name);
    this.name = name;
  }

  /**
   * Constructs the LaTeX code for this command.
   *
   * @return LaTeX code for this command.
   */
  public String render() {
    final var sb = new StringBuilder();

    sb.append('\\'); // LaTeX escape code
    sb.append(name);

    for (var arg : arguments) {
      sb.append(arg.argument());
    }

    if (isNode) {
      sb.append(';');
    }

    return sb.toString();
  }

  //
  // Arguments
  //

  public Command optional(String name) {
    return innerArgument(new OptionalArgument(name));
  }

  public Command node(String name) {
    if (isNode) {
      throw new UnsupportedOperationException("cannot set a command as a node twice");
    }
    isNode = true;
    return innerArgument(new NodeArgument(name));
  }

  public Command argument(String name) {
    return innerArgument(new RegularArgument(name));
  }

  private Command innerArgument(Argument arg) {
    arguments.add(arg);
    return this;
  }

  private interface Argument {

    String argument();
  }


  private record OptionalArgument(String value) implements Argument {

    @Override
    public String argument() {
      return "[%s]".formatted(value);
    }
  }

  private record NodeArgument(String value) implements Argument {

    @Override
    public String argument() {
      return "(%s)".formatted(value);
    }
  }

  private record RegularArgument(String value) implements Argument {

    @Override
    public String argument() {
      return "{%s}".formatted(value);
    }
  }
}
