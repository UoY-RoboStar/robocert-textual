/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.ll.csp;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Generator for binary operations in CSP-M.
 *
 * @author Matt Windsor
 */
public class BinaryGenerator {

  /**
   * Constructs an interrupt operation.
   *
   * @param lhs the left-hand side.
   * @param set the interrupt set.
   * @param rhs the right-hand side.
   * @return the CSP-M interrupt operation.
   */
  public CharSequence interrupt(CharSequence lhs, CharSequence set, CharSequence rhs) {
    return "%s [| %s |> %s".formatted(lhs, set, rhs);
  }

  /**
   * Constructs a hiding operation.
   *
   * @param lhs the left-hand side.
   * @param rhs the right-hand side.
   * @return the CSP-M hiding operation.
   */
  public CharSequence hide(CharSequence lhs, CharSequence rhs) {
    return String.join(" \\ ", lhs, rhs);
  }

  /**
   * Sequential composition.
   *
   * @param args the processes to sequentially compose.
   * @return CSP-M for the sequential composition of the given arguments.
   */
  public CharSequence seq(CharSequence... args) {
    return (args.length == 0) ? "SKIP" : String.join(";\n", args);
  }

  /**
   * Constructs a generalised parallel operation between two processes.
   *
   * @param lhs   the left-hand process.
   * @param alpha the alphabet between lhs and rhs.
   * @param rhs   the right-hand process.
   * @return CSP-M for the generalised parallel.
   */
  public CharSequence genParallel(CharSequence lhs, CharSequence alpha, CharSequence rhs) {
    return genParallel(x -> x, (_l, _r) -> alpha, List.of(lhs, rhs));
  }

  /**
   * Constructs a generalised parallel operation by applying process and alphabet functions to a
   * list of items, nesting the parallels to achieve effective left associativity.
   *
   * @param toProcess mapping from items to processes.
   * @param toAlpha   mapping from pairs of consecutive items to their alphabet.
   * @param items     the list of items to consider (must be non-empty).
   * @param <T>       the type of items to map into processes and operators.
   * @return CSP-M for the list of items.
   */
  public <T> CharSequence genParallel(Function<T, CharSequence> toProcess,
      BiFunction<T, T, CharSequence> toAlpha, List<T> items) {
    if (items.size() == 0) {
      throw new IllegalArgumentException("cannot construct generalised parallel without items");
    }

    final var sb = new StringBuilder();
    final var n = items.size();
    // These parentheses are needed because generalised parallel is non-associative.
    if (2 < n) {
      sb.append("(".repeat(n - 2));
    }

    for (var i = 0; i < n; i++) {
      final T item = items.get(i);
      sb.append(toProcess.apply(item));

      // Close the parentheses as we go
      if (0 < i && i < n - 1) {
        sb.append(")");
      }

      if (i < n - 1) {
        sb.append(" [| ").append(toAlpha.apply(item, items.get(i + 1))).append(" |] ");
      }
    }

    return sb.toString();
  }

}
