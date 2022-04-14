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
   * Sequential composition.
   *
   * @param args the processes to sequentially compose.
   * @return CSP-M for the sequential composition of the given arguments.
   */
  public CharSequence seq(CharSequence... args) {
    return (args.length == 0) ? "SKIP" : String.join(";\n", args);
  }

  /**
   * Constructs a generalised parallel operation by applying process and alphabet functions to a
   * list of items.
   *
   * @param toProcess mapping from items to processes.
   * @param toAlpha   mapping from pairs of consecutive items to their alphabet.
   * @param items     the list of items to consider (must be non-empty).
   * @param <T>       the type of items to map into processes and operators.
   * @return CSP-M for the list of items.
   */
  public <T> CharSequence genParallel(Function<T, CharSequence> toProcess,
      BiFunction<T, T, CharSequence> toAlpha, List<T> items) {
    return bop(toProcess, (l, r) -> "[| %s |]".formatted(toAlpha.apply(l, r)), items);
  }

  /**
   * Constructs a binary operation by applying process and operator functions to a list of items.
   *
   * @param toProcess  mapping from items to processes.
   * @param toOperator mapping from pairs of consecutive items to the operator between them.
   * @param items      the list of items to consider (must be non-empty).
   * @param <T>        the type of items to map into processes and operators.
   * @return CSP-M for the list of items.
   */
  public <T> CharSequence bop(Function<T, CharSequence> toProcess,
      BiFunction<T, T, CharSequence> toOperator, List<T> items) {
    if (items.size() == 0) {
      throw new IllegalArgumentException("cannot construct generalised binary operation without items");
    }

    final var sb = new StringBuilder();
    final var n = items.size();
    for (var i = 0; i < n; i++) {
      final var item = items.get(i);
      sb.append(toProcess.apply(item)).append(" ");
      if (i < n - 1) {
        sb.append(toOperator.apply(item, items.get(i + 1))).append(" ");
      }
    }
    return sb.toString();
  }
}
