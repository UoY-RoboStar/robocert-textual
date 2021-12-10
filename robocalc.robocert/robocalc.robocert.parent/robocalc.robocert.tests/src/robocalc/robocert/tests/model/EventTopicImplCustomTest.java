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

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import circus.robocalc.robochart.TypeRef;
import com.google.inject.Inject;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.TypeFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests that the custom implementation of {@link EventTopic} has the appropriate derived properties.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class EventTopicImplCustomTest {
  @Inject
  private RoboCertFactory certFactory;
  @Inject
  private RoboChartFactory chartFactory;
  @Inject
  private TypeFactory typeFactory;

  private Event event;
  private EventTopic topic;

  @BeforeEach
  void setUp() {
    event = chartFactory.createEvent();
    event.setName("foo");

    topic = certFactory.createEventTopic();
    topic.setEvent(event);
  }


  /**
   * Tests that {@code getParamTypes} on a topic with no type returns an empty type list.
   */
  @Test
  void testGetParamTypes_empty() {
    // By default, the event is set up to not have a type.
    assertThat(topic.getParamTypes(), is(empty()));
  }

  /**
   * Tests that {@code getParamTypes} on a topic with a type returns a singleton type list.
   */
  @Test
  void testGetParamTypes_nonEmpty() {
    event.setType(typeFactory.primRef("int"));
    assertThat(topic.getParamTypes(), contains(event.getType()));
  }
}
