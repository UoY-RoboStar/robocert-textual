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
package robostar.robocert.textual.tests.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.SetPropertyMatcher.universal;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on UniversalMessageSets, and also tests that the factory resolves
 * it correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class UniverseMessageSetImplCustomTest {

  @Inject
  protected RoboCertFactory rf;

  /**
   * Tests to make sure universe gap message sets are active and universal.
   */
  @Test
  void testProperties() {
    assertThat(rf.createUniverseMessageSet(), is(universal().andActive()));
  }
}
