/********************************************************************************
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
 ********************************************************************************/
package robocalc.robocert.tests.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.SetPropertyMatcher.notUniversal;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.SetFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on ExtensionalMessageSets, and also tests that the factory
 * resolves it correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class ExtensionalMessageSetImplCustomTest {

  @Inject
  protected SetFactory sf;
  @Inject
  protected RoboCertFactory rf;

  /**
   * Tests to make sure that empty extensional gap message sets are inactive and non-universal.
   */
  @Test
  void testProperties_Empty() {
    assertThat(sf.empty(), is(notUniversal().andInactive()));
  }

  /**
   * Tests to make sure non-empty extensional gap message sets are active but non-universal.
   */
  @Test
  void testProperties_NonEmpty() {
    assertThat(sf.singleton(rf.createMessage()), is(notUniversal().andActive()));
  }
}
