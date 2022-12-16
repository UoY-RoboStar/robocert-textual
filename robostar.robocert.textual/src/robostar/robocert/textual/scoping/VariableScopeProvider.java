/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.scoping;

import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.Variable;
import com.google.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import robostar.robocert.*;
import robostar.robocert.textual.generator.utils.param.ConstantParameter;
import robostar.robocert.textual.generator.utils.param.TargetParameterResolver;
import robostar.robocert.util.StreamHelper;
import robostar.robocert.util.resolve.ResolveHelper;

/**
 * Provides scopes for variables.
 *
 * @author Matt Windsor
 */
public record VariableScopeProvider(TargetParameterResolver tpResolver, ScopeHelper scopeHelper) {
    @Inject
    public VariableScopeProvider {
        Objects.requireNonNull(tpResolver);
        Objects.requireNonNull(scopeHelper);
    }

    /**
     * Gets a scope for the variable referenced in the given expression.
     *
     * @param expr the variable expression.
     * @return the scope, which contains constants shadowed by memory variables.
     */
    public IScope exprScope(RefExp expr) {
        return Scopes.scopeFor(memVariables(expr), this::computeName, constScope(expr));
    }

    private QualifiedName computeName(Variable v) {
        final var vName = v.getName();
        final var maybeLine = ResolveHelper.containerOfType(v, Lifeline.class);
        final var maybeActor = maybeLine.flatMap(l -> Optional.ofNullable(l.getActor()));
        final var maybeName = maybeActor.flatMap(a -> Optional.ofNullable(a.getName()));
        return maybeName.map(n -> QualifiedName.create(n, vName)).orElseGet(() -> QualifiedName.create(vName));
    }

    /**
     * Gets the memory variables in scope at this expression (eg, within its enclosing specification).
     *
     * @param expr the variable expression.
     * @return the scope, which contains memory variables.
     */
    private List<Variable> memVariables(RefExp expr) {
        return ResolveHelper.containerOfType(expr, Interaction.class).stream().flatMap(this::specMemVariables).toList();
    }

    private Stream<Variable> specMemVariables(Interaction x) {
        // We treat an illegal cross-lifeline use of a variable as a well-formedness violation, not a scoping one.
        final var allLines = x.getLifelines().stream();
        final var allVarLists = allLines.flatMap(l -> Optional.ofNullable(l.getVariables()).stream());
        return allVarLists.flatMap(y -> y.getVars().stream());
    }

    /**
     * Produces a scope containing all constants in scope at an expression.
     *
     * <p>
     * These are the constants available on the target of the expression's enclosing specification.
     * They are available in both unqualified and fully qualified forms.
     *
     * @param expr the constant expression.
     * @return the scope, which contains constant variables.
     */
    private IScope constScope(RefExp expr) {
        return scopeHelper.unifiedScope(constants(expr).toList());
    }

    /**
     * Tries to resolve the constant scope for a constant assignment.
     *
     * @param asst the constant assignment for which we are resolving the scope.
     * @return the scope (may be null).
     */
    public IScope constAssignmentScope(ConstAssignment asst) {
        // The constants must not have an initial value.
        final var consts = constants(asst).filter(v -> v.getInitial() == null).toList();
        return scopeHelper.unifiedScope(consts);
    }

    private Stream<Variable> constants(EObject obj) {
        // constScope and constAssignmentScope have the same effective body, except for filtering out
        // value-instantiated constants in the latter.
        return scopeHelper.specificationGroupOf(obj).stream().flatMap(this::specGroupConstants);
    }

    private Stream<Variable> specGroupConstants(SpecificationGroup group) {
        // TODO(@MattWindsor91): find a way of making it so that we can assign parameters
        final var allParams = tpResolver.parameterisation(group.getTarget());
        return StreamHelper.filter(allParams, ConstantParameter.class).map(ConstantParameter::constant);
    }

}
