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

package robostar.robocert.textual.scoping;

import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.Variable;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import robostar.robocert.textual.generator.utils.param.ConstantParameter;
import robostar.robocert.textual.generator.utils.param.TargetParameterResolver;
import robostar.robocert.ConstAssignment;
import robostar.robocert.Interaction;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.util.StreamHelper;

/**
 * Provides scopes for variables.
 *
 * @author Matt Windsor
 */
public record VariableScopeProvider(
    TargetParameterResolver tpResolver,
    IQualifiedNameProvider qnp
) {
  @Inject
  public VariableScopeProvider {
    Objects.requireNonNull(tpResolver);
    Objects.requireNonNull(qnp);
  }

  /**
   * Gets a scope for the variable referenced in the given expression.
   *
   * @param expr the variable expression.
   * @return the scope, which contains constants shadowed by memory variables.
   */
  public IScope exprScope(RefExp expr) {
    return Scopes.scopeFor(memVariables(expr), constScope(expr));
  }

  /**
   * Gets the memory variables in scope at this expression (eg, within its enclosing specification).
   *
   * @param expr the variable expression.
   * @return the scope, which contains memory variables.
   */
  private List<Variable> memVariables(RefExp expr) {
    return getParent(expr, Interaction.class).stream().flatMap(this::specMemVariables).toList();
  }

  private Stream<Variable> specMemVariables(Interaction x) {
    return Optional.ofNullable(x.getVariables()).stream().flatMap(y -> y.getVars().stream());
  }

  /**
   * Produces a scope containing all of the constants in scope at an expression.
   *
   * These are the constants available on the target of the expression's enclosing specification.
   * They are available in both unqualified and fully qualified forms.
   *
   * @param expr the constant expression.
   * @return the scope, which contains constant variables.
   */
  private IScope constScope(RefExp expr) {
    return unifiedScope(constants(expr).toList());
  }

  /**
   * Tries to resolve the constant scope for a constant assignment.
   *
   * @param asst the constant assignment for which we are resolving the scope.
   *
   * @return the scope (may be null).
   */
  public IScope constAssignmentScope(ConstAssignment asst) {
    // The constants must not have an initial value.
    return unifiedScope(constants(asst).filter(v -> v.getInitial() == null).toList());
  }

  private Stream<Variable> constants(EObject obj) {
    // constScope and constAssignmentScope have the same effective body, except for filtering out
    // value-instantiated constants in the latter.
    return getParent(obj, SpecificationGroup.class).stream().flatMap(this::specGroupConstants);
  }

  private Stream<Variable> specGroupConstants(SpecificationGroup group) {
    // TODO(@MattWindsor91): find a way of making it so that we can assign parameters
    return StreamHelper.filter(tpResolver.parameterisation(group.getTarget()), ConstantParameter.class).map(ConstantParameter::constant);
  }

  /**
   * Calculates a scope with both unqualified and qualified forms.
   *
   * The calculated scope brings every constant into scope on its qualified
   * name, and then (for now) overlays the unqualified names also.  This
   * behaviour may change later on, as it introduces ambiguities that may be
   * resolved in surprising manners.
   *
   * @param it  the iterable of items to bring into scope.
   *
   * @return  the iterator as a scope as described above.
   */
  private IScope unifiedScope(Iterable<? extends EObject> it) {
    // TODO(@MattWindsor91): this shouldn't bring ambiguous names into
    // scope, or there should at least be a validation issue for it.
    return Scopes.scopeFor(it, fullyQualifiedScope(it));
  }

  /**
   * Calculates a scope bringing every given constant into scope on its
   * fully qualified name.
   *
   * @param it  the iterable of items to bring into scope.
   *
   * @return  the iterator as a scope as described above.
   */
  private IScope fullyQualifiedScope(Iterable<? extends EObject> it) {
    return Scopes.scopeFor(it, qnp, IScope.NULLSCOPE);
  }

  private <T extends EObject> Optional<T> getParent(EObject ele, Class<T> clazz) {
    return Optional.ofNullable(EcoreUtil2.getContainerOfType(ele, clazz));
  }
}
