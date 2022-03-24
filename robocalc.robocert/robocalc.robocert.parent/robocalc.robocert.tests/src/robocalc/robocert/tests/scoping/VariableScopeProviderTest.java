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

package robocalc.robocert.tests.scoping;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.Matchers.hasScope;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.scoping.VariableScopeProvider;
import robocalc.robocert.tests.RoboCertInjectorProvider;
import robocalc.robocert.tests.util.DummyVariableFactory;

@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class VariableScopeProviderTest {
  @Inject private RoboCertFactory certFactory;
  @Inject private RoboChartFactory chartFactory;
  @Inject private VariableScopeProvider scope;
  @Inject private DummyVariableFactory varFactory;
  @Inject private ExpressionFactory exprFactory;

  // We assume the differences in how we handle targets will (eventually) be tested elsewhere.
  // This is just a convenient type of target to use here.
  private ControllerDef ctrl;
  private ConstAssignment asst;

  @BeforeEach
  void setUp() {
    ctrl = chartFactory.createControllerDef();

    final var ctgt = certFactory.createControllerTarget();
    ctgt.setController(ctrl);

    asst = certFactory.createConstAssignment();

    final var sgrp = certFactory.createSpecificationGroup();
    sgrp.setTarget(ctgt);
    sgrp.getAssignments().add(asst);
  }

  /** Tests that a constant assignment on an empty controller generates an empty scope. */
  @Test
  void testConstAssignmentScope_empty() {
    assertThat(scope.constAssignmentScope(asst), hasScope());
  }

  /** Tests that value-assigned constants are ignored. */
  @Test
  void testConstAssignmentScope_ignoreValueAssigned() {
    final var vf = varFactory.constantList("foo", "bar");
    vf.getVars().get(0).setInitial(exprFactory.bool(false));
    ctrl.getVariableList().add(vf);

    assertThat(scope.constAssignmentScope(asst), hasScope(vf.getVars().get(1)));
  }
}
