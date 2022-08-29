/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.util;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import com.google.inject.Inject;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

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
import robostar.robocert.EventTopic;
import robostar.robocert.MessageTopic;
import robostar.robocert.OperationTopic;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.util.TypeFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;
import robostar.robocert.util.resolve.ParamTypeResolver;

/**
 * Tests topic parameter type resolution.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class ParamTypeResolverTest {
  // TODO(@MattWindsor91): move to robocert-metamodel

  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private TypeFactory typeFactory;
  @Inject
  private MessageFactory messageFactory;
  @Inject
  private ParamTypeResolver paramTypeRes;

  private Event event;
  private EventTopic eventTopic;

  private OperationSig op;
  private OperationTopic opTopic;
  private Parameter[] opParams;

  @BeforeEach
  void setUp() {
    event = chartFactory.createEvent();
    event.setName("foo");
    eventTopic = messageFactory.eventTopic(event);

    op = chartFactory.createOperationSig();
    op.setName("foo");
    opTopic = certFactory.createOperationTopic();
    opTopic.setOperation(op);

    opParams = new Parameter[3];
    opParams[0] = chartFactory.createParameter();
    opParams[0].setName("a");
    opParams[0].setType(typeFactory.primRef("int"));
    opParams[1] = chartFactory.createParameter();
    opParams[1].setName("b");
    opParams[1].setType(typeFactory.primRef("real"));
    opParams[2] = chartFactory.createParameter();
    opParams[2].setName("c");
    opParams[2].setType(typeFactory.primRef("boolean"));
  }


  /**
   * Tests that {@code ParamTypeResolver} on an event topic with no type returns an empty type
   * list.
   */
  @Test
  void testEmptyEvent() {
    // By default, the event is set up to not have a type.
    assertThat(paramTypes(eventTopic), is(empty()));
  }

  /**
   * Tests that {@code ParamTypeResolver} on a topic with a type returns a singleton type list.
   */
  @Test
  void testGetParamTypes_nonEmpty() {
    event.setType(typeFactory.primRef("int"));
    assertThat(paramTypes(eventTopic), contains(event.getType()));
  }

  //
  // Operations
  //


  /**
   * Tests that {@code getParamTypes} on a topic with no parameters returns an empty type list.
   */
  @Test
  void testGetParamTypes_empty() {
    assertThat(paramTypes(opTopic), is(empty()));
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
      op.getParameters().add(opParams[i]);
      matchers.add(is(opParams[i].getType()));
    }

    assertThat(paramTypes(opTopic), contains(matchers));
  }

  //
  // Utility functions
  //

  private List<Type> paramTypes(MessageTopic t) {
    return paramTypeRes.resolve(t).toList();
  }
}
