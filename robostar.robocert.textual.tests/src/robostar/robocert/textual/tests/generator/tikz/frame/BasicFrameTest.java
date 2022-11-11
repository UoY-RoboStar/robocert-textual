/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.generator.tikz.frame;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.google.inject.Inject;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import robostar.robocert.textual.generator.tikz.frame.BasicFrame;
import robostar.robocert.textual.generator.tikz.matrix.CombinedFragmentRow;
import robostar.robocert.textual.generator.tikz.util.InteractionFlattener.EventType;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests that basic frames lower to the appropriate rows and labels.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class BasicFrameTest {

  @Inject
  private TikzStructureGenerator tikz;

  @Inject
  private Serializer ser;

  /**
   * Tests that row generation produces the expected result.
   *
   * @param type basic frame type.
   */
  @ParameterizedTest
  @EnumSource
  public void TestRow(BasicFrame.Type type) {
    assertThat(frame(type).row(EventType.Entered),
        is(new CombinedFragmentRow(EventType.Entered, 42)));
    assertThat(frame(type).row(EventType.Exited),
        is(new CombinedFragmentRow(EventType.Exited, 42)));
  }


  /**
   * Tests that label generation produces the expected result.
   *
   * @param type basic frame type.
   */
  @ParameterizedTest
  @EnumSource
  public void TestGenerateLabel(BasicFrame.Type type) {
    assertThat(frame(type).generateLabel(tikz, ser), is("\\rc%s".formatted(type.toString())));
  }

  private BasicFrame frame(BasicFrame.Type type) {
    return new BasicFrame(type, 42);
  }
}
