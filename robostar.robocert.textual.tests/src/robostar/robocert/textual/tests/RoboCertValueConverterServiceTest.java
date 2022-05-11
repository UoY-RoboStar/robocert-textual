/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
package robostar.robocert.textual.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.RoboCertValueConverterService;

/** Tests the value converters for the textual language. */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class RoboCertValueConverterServiceTest {
  private IValueConverter<String> conv;

  @BeforeEach
  void setUp() {
    conv = new RoboCertValueConverterService().getInterpolateConverter();
  }

  /**
   * Tests that lexing then emitting the given short literal works correctly.
   *
   * <p>We test for any issues in the whitespace stripping, for fidelity.
   */
  @Test
  void TestShortCode_RoundTripLexEmit() {
    roundTripLexEmit("<$\n\tSTOP\n$>");
    roundTripLexEmit("<$ SKIP $>");
  }

  /**
   * Tests that emitting then lexing the given short literal works correctly.
   *
   * <p>We test for any issues in the whitespace stripping, for fidelity.
   */
  @Test
  void TestShortCode_RoundTripEmitLex() {
    roundTripEmitLex("SKIP");
    roundTripEmitLex("a->\n\tb\n\tc->SKIP");
  }

  private void roundTripLexEmit(String want) {
    assertEquals(want, conv.toString(conv.toValue(want, null)));
  }

  private void roundTripEmitLex(String want) {
    assertEquals(want, conv.toValue(conv.toString(want), null));
  }
}
