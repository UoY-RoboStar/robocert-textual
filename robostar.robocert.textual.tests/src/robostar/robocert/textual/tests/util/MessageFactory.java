/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.util;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.PrimitiveType;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.TypeRef;
import com.google.inject.Inject;
import robostar.robocert.Interaction;
import robostar.robocert.MessageSet;
import robostar.robocert.ModuleTarget;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.SpecificationGroup;

/** Provides ways of creating dummy message specifications. */
public class MessageFactory {
  // TODO(@MattWindsor91): lots of old terminology here, eg 'gap' for 'intraMessages'.

  // TODO(@MattWindsor91): reduce overlap with model MessageFactory;
  // the idea is that that will receive non-dummy factory operations.

  @Inject robostar.robocert.util.MessageFactory mf;
  @Inject RoboChartFactory rc;
  @Inject RoboCertFactory rcert;

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

  public SpecificationGroup group() {
    final var it = rcert.createSpecificationGroup();
    it.getActors().addAll(mf.systemActors());
    it.setTarget(target());
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
