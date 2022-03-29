/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
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
package robocalc.robocert.tests.generator.tockcsp.seq.message;

import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.GeneratesCSPMatcher.generatesCSP;

import com.google.inject.Inject;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
import robocalc.robocert.model.robocert.util.MessageFactory;
import robocalc.robocert.model.robocert.util.SetFactory;
import robocalc.robocert.model.robocert.util.ValueSpecificationFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests the message set CSP generator.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
public class MessageSetGeneratorTest {
  @Inject private MessageSetGenerator msg;
  @Inject private MessageFactory mf;
  @Inject private robocalc.robocert.tests.util.MessageFactory msf;
  @Inject private SetFactory sf;

  @Inject private ValueSpecificationFactory vf;

  /** Tests set generation of an empty extensional message set. */
  @Test
  void generateEmptyExtensional() {
    final var e = sf.empty();
    msf.setupAsGap(e);
    assertThat(e, generatesCSP("{}", msg::generate));
  }

  /** Tests set generation of an simple single-occupant extensional message set. */
  @Test
  void generateSimpleSingletonExtensional() {
		final var actors = msf.group().getActors();

    final var spec = mf.spec(actors.get(0), actors.get(1), mf.eventTopic(msf.intEvent()), vf.integer(42));

    final var e = sf.singleton(spec);
    msf.setupAsGap(e);
    assertThat(e, generatesCSP("{| test::event.out.42 |}", msg::generate));
  }
}
