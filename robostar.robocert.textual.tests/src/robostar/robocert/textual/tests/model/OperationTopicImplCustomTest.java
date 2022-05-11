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

package robostar.robocert.textual.tests.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.OperationTopic;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.TypeFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests that the custom implementation of {@link OperationTopic} has the appropriate derived
 * properties.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class OperationTopicImplCustomTest {
  @Inject private RoboCertFactory certFactory;
  @Inject private RoboChartFactory chartFactory;
  @Inject private TypeFactory typeFactory;

  private OperationSig op;
  private OperationTopic topic;

  private Parameter[] params;

  @BeforeEach
  void setUp() {
    op = chartFactory.createOperationSig();
    op.setName("foo");
    topic = certFactory.createOperationTopic();
    topic.setOperation(op);

    params = new Parameter[3];
    params[0] = chartFactory.createParameter();
    params[0].setName("a");
    params[0].setType(typeFactory.primRef("int"));
    params[1] = chartFactory.createParameter();
    params[1].setName("b");
    params[1].setType(typeFactory.primRef("real"));
    params[2] = chartFactory.createParameter();
    params[2].setName("c");
    params[2].setType(typeFactory.primRef("boolean"));
  }

  /** Tests that {@code getParamTypes} on a topic with no parameters returns an empty type list. */
  @Test
  void testGetParamTypes_empty() {
    assertThat(topic.getParamTypes(), is(empty()));
  }

  /**
   * Tests that {@code getParamTypes} on a topic with some parameters returns the expected list.
   *
   * @param info information about the test repetition.
   */
  @RepeatedTest(3)
  void testGetParamTypes(RepetitionInfo info) {
    final var numParams = info.getCurrentRepetition();
    // This convoluted explicit type is required to get the right resolution below!
    final List<Matcher<? super Type>> matchers = new ArrayList<>(numParams);
    for (int i = 0; i < numParams; i++) {
      op.getParameters().add(params[i]);
      matchers.add(is(params[i].getType()));
    }

    assertThat(topic.getParamTypes(), contains(matchers));
  }
}
