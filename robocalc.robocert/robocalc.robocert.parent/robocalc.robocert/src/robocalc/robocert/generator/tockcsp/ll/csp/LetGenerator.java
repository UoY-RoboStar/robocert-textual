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
package robocalc.robocert.generator.tockcsp.ll.csp;

import java.util.stream.Stream;

/**
 * Handles production of CSP-M let-within code.
 * <p>
 * The usual way in which this class will be used is through calls of the form 'this.let(def1, def2,
 * def3).within(body)'.
 *
 * @author Matt Windsor
 */
public class LetGenerator {

  /**
   * Starts a let-within definition with the given elements.
   *
   * @param elements the elements to have between 'let' and 'within'.
   * @return an object that can be finished with a 'within' call.
   */
  public Let let(CharSequence... elements) {
    return new Let(elements);
  }

  /**
   * Helper class for producing let-within CSP.
   */
  public record Let(CharSequence... elements) {

    /**
     * Adds more elements to this let-within, producing a new one.
     *
     * @param newElements the new elements to add to this let-within.
     * @return a new let-within with the concatenation of old and new elements.
     */
    public Let and(CharSequence... newElements) {
      return new Let(
          Stream.concat(Stream.of(elements), Stream.of(newElements)).toArray(CharSequence[]::new));
    }

    /**
     * Finishes a let-within definition.
     *
     * @param body the 'within' part of the body.
     * @return the finished let-within sequence.
     */
    public CharSequence within(CharSequence body) {
      if (elements.length == 0) {
        return body;
      }

      final var elementStanza = String.join("\n", elements);

      return """
          let
          %s
          within
          %s
          """.formatted(CSPStructureGenerator.indentStrip(elementStanza),
          CSPStructureGenerator.indentStrip(body));
    }
  }
}