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
 *   $author - initial definition
 ******************************************************************************/

package robostar.robocert.textual.tests.util;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.EventTopic;
import robostar.robocert.World;
import robostar.robocert.util.resolve.EventResolver;

public record DummyEventResolver(RoboChartFactory chart) implements EventResolver {
  @Inject
  public DummyEventResolver {
    Objects.requireNonNull(chart);
  }

  @Override
  public Stream<Connection> resolve(EventTopic topic, Actor from, Actor to) {
    final var efrom = topic.getEfrom();
    final var efromName = efrom.getName();
    final var eto = topic.getEto();
    final var etoName = eto.getName();

    // Expose an async bidirectional connection from 'bidi1' to 'bidi2'.
    if ((efromName.equals("bidi1") && etoName.equals("bidi2")) || (efromName.equals("bidi2") && etoName.equals("bidi1"))) {
      final var conn = chart.createConnection();
      conn.setBidirec(true);
      final var rightWayUp = efromName.equals("bidi1");
      conn.setEfrom(rightWayUp ? efrom : eto);
      conn.setEto(rightWayUp ? eto : efrom);
      conn.setFrom(fabricateNode(rightWayUp ? from : to));
      conn.setTo(fabricateNode(rightWayUp ? to : from));
      return Stream.of(conn);
    }
    // TODO(@MattWindsor91): add more events as time goes by.

    return Stream.of();
  }

  private ConnectionNode fabricateNode(Actor a) {
    if (a instanceof World) {
      final var rp = chart.createRoboticPlatformDef();
      rp.setName("RP");
      return rp;
    }
    if (a instanceof ComponentActor c) {
      return c.getNode();
    }
    // this is likely not even reached.
    final var ctrl = chart.createControllerDef();
    ctrl.setName("Ctrl");
    return ctrl;
  }
}
