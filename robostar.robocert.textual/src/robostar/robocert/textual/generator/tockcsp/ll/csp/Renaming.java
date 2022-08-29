/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.ll.csp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder for CSP rename operations.
 *
 * @author Matt Windsor
 */
public class Renaming {

  private final List<Pair> pairs = new ArrayList<>();

  private record Pair(CharSequence from, CharSequence to) {

    @Override
    public String toString() {
      return "%s <- %s".formatted(from, to);
    }
  }

  /**
   * Begins a renaming builder.
   */
  public Renaming() {
  }

  /**
   * Adds a pair to the renaming.
   *
   * @param from the channel to be renamed.
   * @param to   the replacement channel.
   * @return a reference to this renamer.
   */
  public Renaming rename(CharSequence from, CharSequence to) {
    pairs.add(new Pair(from, to));
    return this;
  }

  /**
   * Ends the rename builder, producing a renamed version of the input subject.
   *
   * @param subject CSP-M to be renamed.
   * @return the result of the renaming.
   */
  public CharSequence in(CharSequence subject) {
    if (pairs.isEmpty()) {
      return subject;
    }

    final var rename = buildRenaming();
    return String.join("", subject, rename);
  }

  private String buildRenaming() {
    // As usual, try to fit things on one line, and give up if it's excessively long.
    final var shortTry = pairs.stream().map(Pair::toString)
        .collect(Collectors.joining(", ", "[[ ", " ]]"));
    if (shortTry.length() < 50) {
      return shortTry;
    }

    return pairs.stream().map(x -> CSPStructureGenerator.indentStrip(x.toString()))
        .collect(Collectors.joining(",\n", "[[\n", "\n]]"));
  }
}
