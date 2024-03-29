/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.memory;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;
import com.google.inject.Inject;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import robostar.robocert.textual.generator.tockcsp.core.TemporaryVariableGenerator;

/**
 * Generates loads and stores between local storage and the memory module.
 *
 * <p>Loads are used to put bindings into scope at the CSP level before they appear in expressions.
 * Stores are used to update the memory with bindings captured in arrow actions.
 *
 * @author Matt Windsor
 */
public class LoadStoreGenerator {
  @Inject private TemporaryVariableGenerator bg;
  @Inject private ModuleGenerator mg;

  /**
   * Generates a memory load prefix for a set of referenced expressions.
   *
   * <p>This should be inserted before whatever CSP process is going to reference the bindings.
   *
   * @param references the referenced expressions.
   * @return the CSP-M prefix.
   */
  public CharSequence generateExprLoads(Stream<Expression> references) {
    return generateLoads(references.flatMap(this::getExprVariables));
  }

  /**
   * Generates a memory load prefix for a set of referenced variables.
   *
   * <p>This should be inserted before whatever CSP process is going to reference the bindings.
   *
   * @param references the referenced variables.
   * @return the CSP-M prefix.
   */
  public CharSequence generateLoads(Stream<Variable> references) {
    return generatePrefix(references, "get?", x -> bg.generateInputName(x));
  }

  /**
   * Generates a memory store prefix for all bindings included in this object.
   *
   * <p>This should be inserted AFTER the CSP prefix that produced the bindings (between the '->'
   * and the 'SKIP').
   *
   * @param it the object whose memory stores we are generating.
   * @return the CSP-M prefix.
   */
  public CharSequence generateBindingStores(EObject it) {
    return generateStores(EcoreUtil2.eAllOfType(it, Variable.class).stream());
  }

  /**
   * Generates a memory store prefix for a set of variable references.
   *
   * <p>This should be inserted AFTER the CSP prefix that referenced the variables (between the '->'
   * and the 'SKIP').
   *
   * @param references the variables being referenced by the prefix.
   * @return the CSP-M prefix.
   */
  public CharSequence generateStores(Stream<Variable> references) {
    return generatePrefix(references, "set!", bg::generateExpressionName);
  }

  /**
   * Gets all non-constant variables referenced in expressions within this object.
   *
   * @param it the object to inspect.
   * @return all non-constant variables referenced in expressions within it.
   */
  public Stream<Variable> getExprVariables(EObject it) {
    return EcoreUtil2.eAllOfType(it, RefExp.class).stream()
        .mapMulti((r, p) -> {
          if (r.getRef() instanceof Variable v && v.getModifier() == VariableModifier.VAR) {
            p.accept(v);
          }
        });
  }

  private CharSequence generatePrefix(
      Stream<Variable> bindings, String operator, Function<Variable, CharSequence> getRhs) {
    return String.join(
        "", bindings.distinct().map(x -> generatePrefixItem(x, operator, getRhs)).toList());
  }

  private CharSequence generatePrefixItem(
      Variable b, String operator, Function<Variable, CharSequence> getRhs) {
    return "%s.%s%s -> ".formatted(mg.generateChannelRef(b), operator, getRhs.apply(b));
  }
}
