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

package robocalc.robocert.tests.generator.tockcsp.ll.csp;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.ll.csp.BinaryGenerator;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests generation of binary CSP operators.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class BinaryGeneratorTest {

  @Inject
  private BinaryGenerator gen;

  /**
   * Tests constructing a generalised parallel with one item.
   */
  @Test
  void testGenParallel_OneItem() {
    assertThat(List.of("A"), generatesCSP("A", this::genParallel));
  }

  /**
   * Tests constructing a generalised parallel with two items.
   */
  @Test
  void testGenParallel_TwoItems() {
    assertThat(List.of("A", "B"), generatesCSP("A [| A:[B] |] B", this::genParallel));
  }

  /**
   * Tests constructing a generalised parallel with three items, thereby introducing the need for
   * parentheses.
   */
  @Test
  void testGenParallel_ThreeItems() {
    assertThat(List.of("A", "B", "C"),
        generatesCSP("(A [| A:[B, C] |] B) [| B:[C] |] C", this::genParallel));
  }

  /**
   * Tests constructing a generalised parallel with four items.
   */
  @Test
  void testGenParallel_FourItems() {
    assertThat(List.of("A", "B", "C", "D"),
        generatesCSP("((A [| A:[B, C, D] |] B) [| B:[C, D] |] C) [| C:[D] |] D", this::genParallel));
  }

  private CharSequence genParallel(List<CharSequence> contents) {
    return gen.genParallel(x -> x, "%s:%s"::formatted, contents);
  }
}
