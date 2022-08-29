/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tikz.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the {@link TikzStructureGenerator}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class TikzStructureGeneratorTest {

  @Inject
  private TikzStructureGenerator tsg;

  /**
   * Tests that generating a coordinate provides the expected output.
   */
  @Test
  void testCoordinate() {
    assertThat(tsg.coordinate("name"), is("\\coordinate(name);"));
  }

  /**
   * Tests that generating a node provides the expected output.
   */
  @Test
  void testNode() {
    assertThat(tsg.node("style", "name", "content"), is("\\node[style](name){content};"));
  }
}
