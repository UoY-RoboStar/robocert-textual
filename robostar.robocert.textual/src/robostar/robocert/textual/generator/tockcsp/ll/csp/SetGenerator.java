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
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robostar.robocert.textual.generator.tockcsp.ll.csp;

/**
 * Generates CSP sets and setlike constructs.
 */
public class SetGenerator {
  /**
   * Generates a CSP set comprehension construct.
   *
   * @param lhs  the LHS of the set comprehension.
   * @param rhss the elements of the RHS of the set comprehension.
   * @return CSP-M for the set comprehension.
   */
  public CharSequence setComprehension(CharSequence lhs, CharSequence... rhss) {
    return setlike("{ %s | ".formatted(lhs), " }", rhss);
  }

  /**
   * Generates a CSP non-enumerated set.
   *
   * @param args the contents of the set.
   * @return CSP-M for the set.
   */
  public CharSequence set(CharSequence... args) {
    return setlike("{ ", " }", args);
  }

  /**
   * Generates a CSP enumerated set.
   *
   * @param args the contents of the set.
   * @return CSP-M for the enumerated set.
   */
  public CharSequence enumeratedSet(CharSequence... args) {
    return setlike("{| ", " |}", args);
  }

  /**
   * Generates a tuple.
   *
   * @param args the contents of the tuple.
   * @return CSP-M for the tuple.
   */
  public CharSequence tuple(CharSequence... args) {
    return setlike("(", ")", args);
  }

  /**
   * Generates a singleton set containing the tock event.
   * @return the tock set.
   */
  public CharSequence tock() {
    return set("tock");
  }

  /**
   * Generates a list.
   *
   * @param args the contents of the list.
   * @return CSP-M for the list.
   */
  public CharSequence list(CharSequence... args) {
    return setlike("<", ">", args);
  }

  private CharSequence setlike(CharSequence lhs, CharSequence rhs, CharSequence... args) {
    var body = String.join(", ", args);

    // this is a very rudimentary heuristic
    final var isLong = hasNewlines(args) || 72 < body.length();

    if (isLong) {
      lhs = lhs.toString().stripTrailing();
      rhs = rhs.toString().stripLeading();
      body = CSPStructureGenerator.indentStrip(String.join(",\n", args));
    }

    return String.join(isLong ? "\n" : "", lhs, body, rhs);
  }

  private boolean hasNewlines(CharSequence... args) {
    for (var a : args) {
      if (a.chars().anyMatch(x -> x == 0x0a)) {
        return true;
      }
    }
    return false;
  }
}
