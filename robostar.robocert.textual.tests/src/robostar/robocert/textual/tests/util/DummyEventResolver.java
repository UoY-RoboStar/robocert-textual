/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.util;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;

import robostar.robocert.*;
import robostar.robocert.util.resolve.EventResolver;
import robostar.robocert.util.resolve.EventResolverQuery;

public record DummyEventResolver(RoboChartFactory chart) implements EventResolver {
  @Inject
  public DummyEventResolver {
    Objects.requireNonNull(chart);
  }

  @Override
  public Stream<Connection> resolve(EventResolverQuery q) {
    final var efrom = q.topic().getEfrom();
    final var efromName = efrom.getName();
    final var eto = q.topic().getEto();
    final var etoName = eto.getName();

    // Expose an async bidirectional connection from 'bidi1' to 'bidi2'.
    if ((efromName.equals("bidi1") && etoName.equals("bidi2")) || (efromName.equals("bidi2") && etoName.equals("bidi1"))) {
      final var conn = chart.createConnection();
      conn.setBidirec(true);
      final var rightWayUp = efromName.equals("bidi1");
      conn.setEfrom(rightWayUp ? efrom : eto);
      conn.setEto(rightWayUp ? eto : efrom);
      conn.setFrom(fabricateNode(rightWayUp ? q.from() : q.to()));
      conn.setTo(fabricateNode(rightWayUp ? q.to() : q.from()));
      return Stream.of(conn);
    }
    // TODO(@MattWindsor91): add more events as time goes by.

    return Stream.of();
  }

  private ConnectionNode fabricateNode(MessageEnd a) {
    if (a instanceof Gate) {
      final var rp = chart.createRoboticPlatformDef();
      rp.setName("RP");
      return rp;
    }
    if (a instanceof MessageOccurrence n && n.getActor() instanceof ComponentActor c) {
      return c.getNode();
    }
    // this is likely not even reached.
    final var ctrl = chart.createControllerDef();
    ctrl.setName("Ctrl");
    return ctrl;
  }
}
