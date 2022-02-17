/********************************************************************************
 * Copyright (c) 2021 University of York and others
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
package robocalc.robocert.tests.util;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.PrimitiveType;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.TypeRef;
import com.google.inject.Inject;
import robocalc.robocert.model.robocert.EdgeDirection;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.MessageOccurrence;
import robocalc.robocert.model.robocert.MessageSet;
import robocalc.robocert.model.robocert.MessageTopic;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.SpecificationGroup;
import robocalc.robocert.model.robocert.ValueSpecification;
import robocalc.robocert.model.robocert.util.EdgeFactory;

/** Provides ways of creating dummy message specifications. */
public class MessageFactory {
  // TODO(@MattWindsor91): lots of old terminology here, eg 'gap' for 'intraMessages'.

  // TODO(@MattWindsor91): reduce overlap with model MessageFactory;
  // the idea is that that will receive non-dummy factory operations.

  @Inject EdgeFactory ef;
  @Inject robocalc.robocert.model.robocert.util.MessageFactory mf;
  @Inject RoboChartFactory rc;
  @Inject RoboCertFactory rcert;

  /**
   * Creates an arrow message spec with the given topic, direction, and arguments, with a fake
   * sequence-group context.
   *
   * @param t the desired topic.
   * @param dir the desired direction.
   * @param args the desired arguments.
   * @return a constructed arrow message spec.
   */
  public Message arrowSpec(MessageTopic t, EdgeDirection dir, ValueSpecification... args) {
    final var s = mf.spec(t, ef.edge(dir), args);
    arrowParent().setMessage(s);
    return s;
  }

  private MessageOccurrence arrowParent() {
    final var fragment = rcert.createOccurrenceFragment();
    final var mo = rcert.createMessageOccurrence();
    mo.setFragment(fragment);
    seq().getFragments().add(fragment);

    return mo;
  }

  /**
   * Hoists the given set into being the gap set for an UntilFragment that is attached to the test
   * subsequence.
   *
   * <p>Acts in-place.
   *
   * @param g the set to hoist.
   */
  public void setupAsGap(MessageSet g) {
    final var it = rcert.createUntilFragment();
    it.setIntraMessages(g);
    seq().getFragments().add(it);
  }

  public Event intEvent() {
    final var it = rc.createEvent();
    it.setName("event");
    it.setType(intTypeRef());
    return it;
  }

  private TypeRef intTypeRef() {
    final var it = rc.createTypeRef();
    it.setRef(intType());
    return it;
  }

  private PrimitiveType intType() {
    final var it = rc.createPrimitiveType();
    it.setName("int");
    return it;
  }

  private Interaction seq() {
    final var it = rcert.createInteraction();
    it.setGroup(group());
    return it;
  }

  private SpecificationGroup group() {
    final var it = rcert.createSpecificationGroup();
    it.setTarget(target());
    it.getActors().addAll(mf.systemActors());
    return it;
  }

  /** @return a mock target. */
  public ModuleTarget target() {
    final var it = rcert.createModuleTarget();
    it.setModule(rcModule());
    return it;
  }

  /** @return a mock RoboChart module. */
  public RCModule rcModule() {
    final var it = rc.createRCModule();
    it.setName("test");
    return it;
  }
}
