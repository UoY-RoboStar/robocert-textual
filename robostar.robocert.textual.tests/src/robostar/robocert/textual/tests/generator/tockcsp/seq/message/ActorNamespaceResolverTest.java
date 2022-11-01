/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.tockcsp.seq.message;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.generator.tockcsp.seq.message.ActorNamespaceResolver;
import robostar.robocert.textual.tests.util.RoboCertCustomInjectorProvider;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.util.TargetFactory;

/**
 * Tests the actor namespace resolver.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class ActorNamespaceResolverTest {

  @Inject
  private ActorNamespaceResolver res;
  @Inject
  private RoboChartFactory rchart;
  @Inject
  private RoboCertFactory rcert;
  @Inject
  private TargetFactory targetFactory;

  /**
   * Tests prefix generation of an arrow message set concerning an integer event against an argument
   * list containing a wildcard ('any') argument only.
   */
  @Test
  void resolveControllerInPackage() {
    final var pkg = rchart.createRCPackage();
    pkg.setName("pkg");

    final var cdef = rchart.createControllerDef();
    cdef.setName("ctrl");
    pkg.getControllers().add(cdef);

    final var sg = rcert.createSpecificationGroup();
    sg.setTarget(targetFactory.inController(cdef));
    final var ta = rcert.createTargetActor();
    ta.setGroup(sg);

    assertThat(res.namespace(ta), is("pkg::ctrl"));
  }
}
