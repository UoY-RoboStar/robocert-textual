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
 *   Alvaro Miyazawa and Pedro Ribeiro - initial definition in RoboChart
 *   Matt Windsor - port to RoboCert
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.Context;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedModuleGenerator;
import robostar.robocert.util.resolve.ModuleResolver;

/**
 * Generates bodies of in-module targets.
 *
 * @author Matt Windsor
 */
public class InModuleTargetBodyGenerator extends
    CollectionTargetBodyGenerator<RCModule, RoboticPlatformDef, Controller> {
  @Inject
  protected CTimedModuleGenerator modGen;

  @Inject
  protected ModuleResolver modRes;

  private boolean isAsyncConnection(Connection c) {
    return c.isAsync() && !(c.getTo() instanceof RoboticPlatform)
        && !(c.getFrom() instanceof RoboticPlatform);
  }

  @Override
  protected String namespace(RCModule element) {
    return element.getName();
  }

  @Override
  protected RoboticPlatformDef context(RCModule element) {
    return modRes.platform(element).orElse(null);
  }

  @Override
  protected List<Controller> components(RCModule element) {
    return modRes.controllers(element).toList();
  }

  @Override
  protected List<Connection> connections(RCModule element) {
    return element.getConnections();
  }

  @Override
  protected Context definition(Controller comp) {
    return defRes.resolve(comp);
  }

  @Override
  protected String name(Controller comp) {
    return gu.ctrlName(comp);
  }

  @Override
  protected Class<Controller> compClass() {
    return Controller.class;
  }

  @Override
  protected CharSequence wrapInner(RCModule element, RoboticPlatformDef ctx, CharSequence body) {
    final Stream<CharSequence> vars = gu.allLocalVariables(ctx).stream().mapMulti((v, c) -> {
      final var init = v.getInitial();
      if (init != null) {
        c.accept("%s!%s ->".formatted(intSet(v), exprGen.compileExpression(init, element)));
      }
    });

    return Stream.concat(vars, Stream.of(body)).collect(Collectors.joining("\n"));
  }

  @Override
  protected CharSequence wrapOuter(RCModule element, RoboticPlatformDef ctx, CharSequence body) {
    // Controllers in a module can have async communications, so this sets up the buffers for them.

    final var async = element.getConnections().stream().filter(this::isAsyncConnection).toList();
    if (async.isEmpty()) {
      return body;
    }

    final var bidirecAsync = async.stream().filter(Connection::isBidirec).toList();
    final var syncset = async.stream().flatMap(
            c -> Stream.of(csp.namespaced(gu.connectionNodeName(c.getTo()), gu.eventId(c.getEto())),
                csp.namespaced(gu.connectionNodeName(c.getFrom()), gu.eventId(c.getEfrom()))))
        .toArray(CharSequence[]::new);
    return csp.bins()
        .genParallel(csp.sets().tuple(modGen.composeBuffers(async, bidirecAsync, element)),
            csp.enumeratedSet(syncset),
            csp.let(modGen.compileBuffers(async, bidirecAsync, element)).within(body));
  }
}

