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
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.Renaming;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests generation of CSP renaming stanzas.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class RenamingTest {

  @Inject
  private CSPStructureGenerator csp;

  /**
   * Tests that an empty rename generates no rename clause.
   */
  @Test
  void testNoRenames() {
    final var renamer = csp.renaming();
    assertThat(renamer, generatesCSP("P", this::rename));
  }

  /**
   * Tests that a single rename generates an appropriate rename clause.
   */
  @Test
  void testOneRename() {
    final var renamer = csp.renaming().rename("a", "b");
    assertThat(renamer, generatesCSP("P[[ a <- b ]]", this::rename));
  }

  /**
   * Tests that a multiple renaming generates an appropriate rename clause.
   */
  @Test
  void testTwoRenames() {
    final var renamer = csp.renaming().rename("a", "b").rename("c", "d");
    assertThat(renamer, generatesCSP("P[[ a <- b, c <- d ]]", this::rename));
  }

  private CharSequence rename(Renaming r) {
    return r.in("P");
  }
}
