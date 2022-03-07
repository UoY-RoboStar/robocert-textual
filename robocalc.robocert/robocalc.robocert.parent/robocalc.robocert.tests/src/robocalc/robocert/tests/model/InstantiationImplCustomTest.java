/*******************************************************************************
 * Copyright (c) 2021 University of York and others
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
package robocalc.robocert.tests.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.IsIntegerExpWithValue.intExprWithValue;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on Instantiations, and also tests that the factory resolves it
 * correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class InstantiationImplCustomTest {

  @Inject protected ExpressionFactory xf;

  @Inject protected RoboCertFactory rf;

  @Inject protected RoboChartFactory cf;

  @Test
  public void testGetConstant() {
    final var x1 = cf.createVariable();
    x1.setName("foo");
    final var x2 = EcoreUtil.copy(x1);
    final var y1 = cf.createVariable();
    y1.setName("bar");
    final var y2 = EcoreUtil.copy(y1);

    final var inst = rf.createInstantiation();
    final var assts = inst.getAssignments();

    final var asst1 = rf.createConstAssignment();
    asst1.getConstants().addAll(List.of(x1, y2));
    asst1.setValue(xf.integer(42));
    assts.add(asst1);

    final var asst2 = rf.createConstAssignment();
    asst2.getConstants().add(x2);
    asst2.setValue(xf.integer(24));
    assts.add(asst2);

    assertThat(inst.getConstant(x1), is(intExprWithValue(42)));
    assertThat(inst.getConstant(x2), is(intExprWithValue(24)));
    assertThat(inst.getConstant(y1), is(nullValue()));
    assertThat(inst.getConstant(y2), is(intExprWithValue(42)));
  }
}
