/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.generator.tockcsp.ll.csp;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import robocalc.robocert.generator.tockcsp.ll.csp.LetGenerator.Let;

/**
 * Generates common CSP-M structures such as modules and timed sections.
 * <p>
 * Use of this class makes uniform indenting and formatting easier.
 *
 * @author Matt Windsor
 */
public record CSPStructureGenerator(BinaryGenerator bins, LetGenerator letGenerator,
                                    SetGenerator sets) {

  /**
   * Constructs a CSP structure generator.
   *
   * @param bins sub-generator for binary operations.
   * @param letGenerator sub-generator for let expressions.
   * @param sets sub-generator for set expressions.
   */
  @Inject
  public CSPStructureGenerator {
    Objects.requireNonNull(bins);
    Objects.requireNonNull(letGenerator);
    Objects.requireNonNull(sets);
  }

  /**
   * Shorthand for starting to build a let expression.
   *
   * @param elements the elements of the top of the let expression.
   * @return an object for constructing the let expression.
   */
  public Let let(CharSequence... elements) {
    return letGenerator.let(elements);
  }

  /**
   * @return the RoboChart encoding of timestop.
   */
  public CharSequence timestop() {
    return "USTOP";
  }

  /**
   * @return the RoboChart encoding of timed skip.
   */
  public CharSequence skip() {
    return "SKIP";
  }

  /**
   * Generates a comment before something else.
   *
   * @param comment the comment to add.
   * @param body    the body to comment.
   * @return CSP-M for the commented body.
   */
  public CharSequence commented(CharSequence comment, CharSequence body) {
    return "{- %s -} %s".formatted(comment, body);
  }

  /**
   * Generates a module instance declaration.
   *
   * @param name the name of the instance.
   * @param body the body of the instance.
   * @return CSP-M for the instance.
   */
  public CharSequence instance(CharSequence name, CharSequence body) {
    return definition("instance %s".formatted(name), body);
  }

  /**
   * Generates a process/set/function definition.
   *
   * @param head the header of the item.
   * @param body the body of the item.
   * @return CSP-M for the process.
   */
  public CharSequence definition(CharSequence head, CharSequence body) {
    return String.join(" = ", head, body);
  }

  /**
   * Generates a CSP function-like construct.
   *
   * @param name the name of the function/process/module.
   * @param args the arguments of the function/process/header.
   * @return CSP-M for the process header.
   */
  public CharSequence function(CharSequence name, CharSequence... args) {
    return args.length == 0 ? name : String.join("", name, tuple(args));
  }

  /**
   * Generates a CSP union construct.
   *
   * @param lhs CSP-M for the left-hand side.
   * @param rhs CSP-M for the right-hand side.
   * @return CSP-M for the union.
   */
  public CharSequence union(CharSequence lhs, CharSequence rhs) {
    return function("union", lhs, rhs);
  }

  /**
   * Generates a CSP iterated union construct.
   *
   * @param setset the set-ofset CSP-M.
   * @return CSP-M for the union.
   */
  public CharSequence iteratedUnion(CharSequence setset) {
    return function("Union", setset);
  }

  /**
   * Generates an iterated alphabetised parallel over the set [0, count), where the alphabet and
   * process definitions are handled by the functions alphaFun(x) and procFun(x) respectively (where
   * x is an element of the set).
   * <p>
   * This will generate notationally optimised CSP-M in the special cases of 0, 1, and 2 elements.
   *
   * @param indices  set of indices (in CSP-M).
   * @param alphaFun name of the alphabetisation function (in CSP-M).
   * @param procFun  name of the process function (in CSP-M).
   * @return an iterated alphabetised parallel composition of procFun(X), with alphabet alphaFun(X),
   * where X comes from indices.
   */
  public CharSequence iterAlphaParallel(CharSequence indices, CharSequence alphaFun,
      CharSequence procFun) {
    return "|| x: %s @ [%s(x)] %s(x)".formatted(indices, alphaFun, procFun);
  }

  /**
   * Generates a CSP set comprehension construct.
   *
   * @param lhs  the LHS of the set comprehension.
   * @param rhss the elements of the RHS of the set comprehension.
   * @return CSP-M for the set comprehension.
   */
  public CharSequence setComprehension(CharSequence lhs, CharSequence... rhss) {
    return sets.setComprehension(lhs, rhss);
  }

  /**
   * Generates a CSP non-enumerated set.
   *
   * @param args the contents of the set.
   * @return CSP-M for the set.
   */
  public CharSequence set(CharSequence... args) {
    return sets.set(args);
  }

  /**
   * Generates a CSP enumerated set.
   *
   * @param args the contents of the set.
   * @return CSP-M for the enumerated set.
   */
  public CharSequence enumeratedSet(CharSequence... args) {
    return sets.enumeratedSet(args);
  }

  /**
   * Generates a tuple.
   *
   * @param args the contents of the tuple.
   * @return CSP-M for the tuple.
   */
  public CharSequence tuple(CharSequence... args) {
    return sets.tuple(args);
  }

  /**
   * Generates a list.
   *
   * @param args the contents of the list.
   * @return CSP-M for the list.
   */
  public CharSequence list(CharSequence... args) {
    return sets.list(args);
  }

  /**
   * Sequential composition.
   *
   * @param args the processes to sequentially compose.
   * @return CSP-M for the sequential composition of the given arguments.
   */
  public CharSequence seq(CharSequence... args) {
    return bins.seq(args);
  }

  /**
   * Opens a CSP-M module.
   *
   * @param name the name of the module.
   * @return a factory for building the module.
   */
  public Module module(CharSequence name) {
    return new Module(name);
  }

  /**
   * Generates a timed section with the appropriate timing function.
   *
   * @param inner the inner body of the timed section.
   * @return CSP-M for the timed section.
   */
  public CharSequence timed(CharSequence inner) {
    return """
        Timed(OneStep) {
        %s
        }
        """.formatted(indentStrip(inner.toString()));
  }

  /**
   * Produces a basic datatype.
   *
   * @param name     the name of the datatype.
   * @param elements the discriminants of the datatype (must be at least one).
   * @return CSP-M for the datatype.
   */
  public CharSequence datatype(CharSequence name, CharSequence... elements) {
    // TODO(@MattWindsor91): break overly long datatypes.
    if (elements.length < 1) {
      throw new IllegalArgumentException(
          "datatype %s must contain at least one element".formatted(name));
    }
    return definition("datatype %s".formatted(name), String.join(" | ", elements));
  }

  /**
   * Lifts a body into a timed section if the given Boolean is true.
   *
   * @param isTimed the boolean.
   * @param inner   the inner body of the possibly-timed section.
   * @return CSP-M for the possibly-timed section.
   */
  public CharSequence timedIf(boolean isTimed, CharSequence inner) {
    return isTimed ? timed(inner) : inner;
  }

  /**
   * Generates a namespaced concatenation of various naming elements.
   *
   * @param elements the elements to join.
   * @return the namespace-joined string.
   */
  public CharSequence namespaced(CharSequence... elements) {
    return String.join("::", elements);
  }

  /**
   * Generates the bare-bones outer structure of a CSP assertion.
   *
   * @param isNegated whether the assertion is negated.
   * @return CSP-M for the assertion.
   */
  public CharSequence assertion(boolean isNegated, CharSequence body) {
    return "assert%s %s".formatted(isNegated ? " not" : "", body);
  }

  /**
   * Generates a refinement with a custom model.
   *
   * @param lhs   the left-hand side of the refinement.
   * @param rhs   the right-hand side of the refinement.
   * @param model the model of the refinement (usually 'T' for traces).
   * @return CSP-M for the refinement.
   */
  public CharSequence refine(CharSequence lhs, CharSequence rhs, CharSequence model) {
    return "%s [%s= %s".formatted(lhs, model, rhs);
  }

  /**
   * Appends a tau-priority-tock pragma to the given CSP.
   *
   * @param it the assertion CSP to extend with the pragma.
   * @return the pragma-modified CSP.
   */
  public CharSequence tauPrioritiseTock(CharSequence it) {
    return "%s :[tau priority]: {tock}".formatted(it);
  }

  public CharSequence innerJoin(Stream<CharSequence> elements) {
    return elements.collect(Collectors.joining("\n"));
  }

  public static String indentStrip(CharSequence cs) {
    return cs.toString().indent(INDENT).stripTrailing();
  }

  private static final int INDENT = 2;
}
