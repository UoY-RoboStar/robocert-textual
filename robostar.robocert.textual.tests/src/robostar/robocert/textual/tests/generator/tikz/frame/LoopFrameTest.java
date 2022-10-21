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

import circus.robocalc.robochart.Expression;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.generator.tikz.frame.LoopFrame;
import robostar.robocert.textual.generator.tikz.util.TikzStructureGenerator;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;
import robostar.robocert.util.ExpressionFactory;

/**
 * Tests that basic frames lower to the appropriate rows and labels.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class LoopFrameTest {

  @Inject
  private TikzStructureGenerator tikz;

  @Inject
  private Serializer ser;

  @Inject
  private RoboCertFactory rf;

  @Inject
  private ExpressionFactory ef;

  @Inject
  private Provider<XtextResourceSet> setProvider;

  /**
   * Tests that label generation for an unnamed loop produces the expected result.
   */
  @Test
  public void TestGenerateLabelUnnamed() {
    assertThat(frame(null, null, null).generateLabel(tikz, ser), is("\\rcloop{}"));

    // High bound defaults to infinity
    assertThat(frame(null, ef.integer(2), null).generateLabel(tikz, ser), is("\\rcloop{(2, *)}"));

    // Low bound defaults to high bound
    assertThat(frame(null, null, ef.integer(4)).generateLabel(tikz, ser), is("\\rcloop{(4)}"));
    assertThat(frame(null, ef.integer(2), ef.integer(4)).generateLabel(tikz, ser),
        is("\\rcloop{(2, 4)}"));
  }

  /**
   * Tests that label generation for a named loop produces the expected result.
   */
  @Test
  public void TestGenerateLabelNamed() {
    assertThat(frame("a", null, null).generateLabel(tikz, ser), is("\\rcnamedloop{a}{}"));

    // High bound defaults to infinity
    assertThat(frame("b", ef.integer(2), null).generateLabel(tikz, ser),
        is("\\rcnamedloop{b}{(2, *)}"));

    // Low bound defaults to high bound
    assertThat(frame("c", null, ef.integer(4)).generateLabel(tikz, ser),
        is("\\rcnamedloop{c}{(4)}"));

    // Checking for name sanitisation
    assertThat(frame("d_e", ef.integer(2), ef.integer(4)).generateLabel(tikz, ser),
        is("\\rcnamedloop{d\\_e}{(2, 4)}"));
  }

  private LoopFrame frame(String name, Expression lo, Expression hi) {
    // TODO(@MattWindsor91): could this be factored out into a factory?
    final var result = rf.createLoopFragment();
    result.setName(name);

    final var bnd = rf.createDiscreteBound();
    bnd.setLower(lo);
    bnd.setUpper(hi);
    result.setBound(bnd);

    // This is necessary to get the serializer to work peoprly.
    final var xr = setProvider.get();
    final var res = xr.createResource(URI.createURI("test.rcert"));
    res.getContents().add(result);

    return new LoopFrame(result, 42);
  }
}
