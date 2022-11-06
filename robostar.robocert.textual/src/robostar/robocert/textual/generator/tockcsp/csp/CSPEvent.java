/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.csp;

import java.util.Arrays;

/**
 * Builder for CSP events.
 * <p>
 * For now, the API is to use the methods to construct an event through a pipeline, then to call
 * {@code toString} to get the CSP-M code.
 *
 * @author Matt Windsor
 */
public record CSPEvent(String head, Suffix... tail) {

  /**
   * Creates a new CSP event by dot-appending values to this one.
   *
   * @param end array of new values to dot-append to the CSP event.
   * @return the new object resulting from dot-appending the given suffixes.
   */
  public CSPEvent dot(String... end) {
    return appendWithOperator(Operator.Dot, end);
  }

  /**
   * Creates a new CSP event by input-appending values to this one.
   *
   * @param end array of new values to input-append to the CSP event.
   * @return the new object resulting from input-appending the given suffixes.
   */
  public CSPEvent input(String... end) {
    return appendWithOperator(Operator.Input, end);
  }

  /**
   * Creates a new CSP event by output-appending values to this one.
   *
   * @param end array of new values to output-append to the CSP event.
   * @return the new object resulting from output-appending the given suffixes.
   */
  public CSPEvent output(String... end) {
    return appendWithOperator(Operator.Output, end);
  }

  /**
   * Creates a new CSP event by appending values to this one with a given operator.
   *
   * @param op  operator to use when appending.
   * @param end array of new values to append to the CSP event.
   * @return the new object resulting from appending the given suffixes.
   */
  public CSPEvent appendWithOperator(Operator op, String... end) {
    return append(Arrays.stream(end).map(x -> new Suffix(op, x)).toArray(Suffix[]::new));
  }

  /**
   * Creates a new CSP event by adding the given array of suffixes to this one.
   *
   * @param end array of new items to add to the CSP event.
   * @return the new object resulting from appending the given suffixes.
   */
  public CSPEvent append(Suffix... end) {
    final var ntail = Arrays.copyOf(tail, tail.length + end.length);
    System.arraycopy(end, 0, ntail, tail.length, end.length);
    return new CSPEvent(head, ntail);
  }

  @Override
  public String toString() {
    final var sb = new StringBuilder(head);
    for (var s : tail) {
      sb.append(s.toString());
    }
    return sb.toString();
  }

  /**
   * Information about a suffix of a CSP event.
   */
  public record Suffix(Operator op, String contents) {

    @Override
    public String toString() {
      return op.toString() + contents;
    }
  }

  /**
   * Enumeration of operators that can be used to join values to a CSP event.
   */
  public enum Operator {
    /**
     * The . event operator.
     */
    Dot,
    /**
     * The ? event operator.
     */
    Input,
    /**
     * The ! event operator.
     */
    Output,
    ;

    @Override
    public String toString() {
      return switch (this) {
        case Dot -> ".";
        case Input -> "?";
        case Output -> "!";
      };
    }
  }
}
