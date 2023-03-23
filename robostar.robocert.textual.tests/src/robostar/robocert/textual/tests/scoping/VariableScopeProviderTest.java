/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.scoping;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.common.matchers.Matchers.hasScope;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.ConstAssignment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.robochart.ExpressionFactory;
import robostar.robocert.textual.scoping.VariableScopeProvider;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;
import robostar.robocert.textual.tests.common.DummyVariableFactory;

@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class VariableScopeProviderTest {

  @Inject
  private RoboCertFactory certFac;
  @Inject
  private RoboChartFactory chartFac;
  @Inject
  private TargetFactory tgtFac;
  @Inject
  private VariableScopeProvider scope;
  @Inject
  private DummyVariableFactory varFac;
  @Inject
  private ExpressionFactory exprFac;

  // We assume the differences in how we handle targets will (eventually) be tested elsewhere.
  // This is just a convenient type of target to use here.
  private ControllerDef ctrl;
  private ConstAssignment asst;

  @BeforeEach
  void setUp() {
    ctrl = chartFac.createControllerDef();

    asst = certFac.createConstAssignment();

    final var sgrp = certFac.createSpecificationGroup();
    sgrp.setTarget(tgtFac.controller(ctrl));
    sgrp.getAssignments().add(asst);
  }

  /**
   * Tests that a constant assignment on an empty controller generates an empty scope.
   */
  @Test
  void testConstAssignmentScope_empty() {
    assertThat(scope.constAssignmentScope(asst), hasScope());
  }

  /**
   * Tests that value-assigned constants are ignored.
   */
  @Test
  void testConstAssignmentScope_ignoreValueAssigned() {
    final var vf = varFac.constantList("foo", "bar");
    vf.getVars().get(0).setInitial(exprFac.bool(false));
    ctrl.getVariableList().add(vf);

    assertThat(scope.constAssignmentScope(asst), hasScope(vf.getVars().get(1)));
  }
}
