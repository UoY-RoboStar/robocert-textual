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
 *   $author - initial definition
 ******************************************************************************/

package robocalc.robocert.tests.generator.tockcsp.seq.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.seq.fragment.DurationFragmentHeaderGenerator;
import robocalc.robocert.model.robocert.DurationFragment;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.util.ExpressionFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the duration fragment header CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class DurationFragmentHeaderGeneratorTest {

  /**
   * The system under test.
   */
  @Inject
  private DurationFragmentHeaderGenerator gen;

  @Inject
  private RoboCertFactory factory;

  @Inject
  private ExpressionFactory exprs;

  private DurationFragment fragment;

  @BeforeEach
  void setUp() {
    final var inner = factory.createInteractionOperand();
    fragment = factory.createDurationFragment();
    fragment.setBody(inner);

  }

  @Test
  void noBounds() {
    assertThat(fragment, generatesCSPDurationHeader("{- no bounds -}"));
  }

  @Test
  void lowerBound() {
    fragment.setLowerBound(exprs.integer(3));
    assertThat(fragment, generatesCSPDurationHeader("DurationLB(3)"));
  }

  @Test
  void upperBound() {
    fragment.setUpperBound(exprs.integer(5));
    assertThat(fragment, generatesCSPDurationHeader("DurationUB(5)"));
  }

  @Test
  void bothBounds() {
    fragment.setLowerBound(exprs.integer(4));
    fragment.setUpperBound(exprs.integer(6));
    assertThat(fragment, generatesCSPDurationHeader("Duration(4, 6)"));
  }

  private Matcher<DurationFragment> generatesCSPDurationHeader(String expected) {
    return generatesCSP(expected, gen::generate);
  }
}
