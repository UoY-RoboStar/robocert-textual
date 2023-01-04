/*
 * Copyright (c) 2022, 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.scoping;

import circus.robocalc.robochart.Enumeration;
import java.util.Objects;

import java.util.stream.Stream;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import com.google.inject.Inject;

import circus.robocalc.robochart.EnumExp;
import circus.robocalc.robochart.RoboChartPackage.Literals;
import circus.robocalc.robochart.textual.scoping.RoboChartScopeProvider;
import robostar.robocert.CertPackage;
import robostar.robocert.util.StreamHelper;
import robostar.robocert.util.resolve.ResolveHelper;

/**
 * Provides scopes for RoboChart enumerations.
 *
 * @author Matt Windsor
 */
public record EnumScopeProvider(ScopeHelper helper, RoboChartScopeProvider chart) {

  /**
   * Constructs an EnumScopeProvider.
   *
   * @param helper scope helper.
   * @param chart  RoboChart scope provider.
   */
  @Inject
  public EnumScopeProvider {
    Objects.requireNonNull(helper);
    Objects.requireNonNull(chart);
  }

  /**
   * Produces a scope for part of an enumeration expression.
   *
   * @param exp enumeration expression.
   * @param ref reference of what is being resolved within the expression.
   * @return a scope corresponding to the given reference and expression.
   */
  public IScope exprScope(EnumExp exp, EReference ref) {
    // Usually we delegate to RoboChart; for types, we do a little extra.
    final var chartScope = chart.getScope(exp, ref);
    return ref == Literals.ENUM_EXP__TYPE ? typeScope(exp, chartScope) : chartScope;
  }

  private IScope typeScope(EnumExp exp, IScope chartScope) {
    // We want to add into scope any explicitly-imported enumerations.
    final var pkg = ResolveHelper.containerOfType(exp, CertPackage.class);
    final var enums = pkg.stream().flatMap(this::packageEnums).toList();
    return Scopes.scopeFor(enums, chartScope);
  }

  private Stream<Enumeration> packageEnums(CertPackage pkg) {
    return pkg.getRcImports().stream().flatMap(i -> StreamHelper.filter(i.getTypes().stream(), Enumeration.class));
  }
}
