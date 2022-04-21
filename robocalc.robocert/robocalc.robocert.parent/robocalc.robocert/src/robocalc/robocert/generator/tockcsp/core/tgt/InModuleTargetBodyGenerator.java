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
import java.util.LinkedList;
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
import robocalc.robocert.generator.tockcsp.ll.csp.Renaming;
import robocalc.robocert.model.robocert.util.DefinitionResolver;

/**
 * Generates bodies of in-module targets.
 *
 * @author Matt Windsor
 */
public class InModuleTargetBodyGenerator extends
    CollectionTargetBodyGenerator<RCModule, RoboticPlatformDef, Controller> {
  @Inject
  protected DefinitionResolver defResolve;

  @Inject
  protected CTimedModuleGenerator modGen;

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
    return defResolve.platform(element).orElse(null);
  }

  @Override
  protected List<Controller> components(RCModule element) {
    return defResolve.controllers(element).toList();
  }

  @Override
  protected List<Connection> connections(RCModule element) {
    return element.getConnections();
  }

  @Override
  protected Context definition(Controller comp) {
    return defResolve.resolve(comp);
  }

  @Override
  protected String name(Controller comp) {
    return gu.ctrlName(comp);
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

  @Override
  protected void renameConnection(Renaming renaming, LinkedList<String> chanset, String ns,
      Controller ctrl, Connection c) {
    final var source = c.getFrom();
    final var target = c.getTo();
    final var esource = c.getEfrom();
    final var etarget = c.getEto();

    // Unlike the RoboChart semantics, we only rename to fuse synchronous connections.  We don't
    // rename connections heading to the robotic platform.
    if (c.isAsync()) {
      return;
    }

    if (source instanceof Controller src) {
      final var srcName = csp.namespaced(ns, gu.ctrlName(src), gu.eventId(esource)).toString();
      if (src == ctrl) {
        // this event needs to be added because we are using generalised parallelism
        chanset.add(srcName);
      } else if (target instanceof Controller tgt && tgt == ctrl) {
        // synchronous inter-controller communication; swap in and out to match source
        final var tgtName = csp.namespaced(ns, gu.ctrlName(tgt), gu.eventId(etarget)).toString();

        renaming.rename(tgtName + ".in", srcName + ".out")
            .rename(tgtName + ".out", srcName + ".in");
        chanset.add(srcName);
      }
    }
  }
}

