/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.generator.tockcsp.csp;

import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.GeneratesCSPMatcher.generatesCSP;

import org.junit.jupiter.api.Test;
import robostar.robocert.textual.generator.tockcsp.csp.CSPEvent;

/**
 * Tests the CSP event builder notation.
 *
 * @author Matt Windsor
 */
public class CSPEventTest {
  @Test
  public void TestToString_Mixed() {
    final var evt = new CSPEvent("chan").in("x").dot("y", "z").out("a", "b");

    assertThat(evt, generatesCSP("chan?x.y.z!a!b", CSPEvent::toString));
  }
}
