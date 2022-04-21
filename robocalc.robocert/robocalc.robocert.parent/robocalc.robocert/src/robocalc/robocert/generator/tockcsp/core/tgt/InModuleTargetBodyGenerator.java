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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedModuleGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.Renaming;

/**
 * Generates bodies of in-module targets.
 *
 * @author Matt Windsor
 */
public class InModuleTargetBodyGenerator extends
    CollectionTargetBodyGenerator<RCModule, RoboticPlatformDef> {

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
  protected Stream<CharSequence> componentVars(RCModule element) {
    return defResolve.controllers(element).flatMap(
        c -> gu.requiredVariables(defResolve.resolve(c)).stream()
            .map(v -> csp.namespaced(gu.ctrlName(c), extSet(v))));
  }

  @Override
  protected CharSequence innerBody(String ns, RCModule element, RoboticPlatformDef ctx) {
    final Stream<CharSequence> vars = gu.allLocalVariables(ctx).stream().mapMulti((v, c) -> {
      final var init = v.getInitial();
      if (init != null) {
        c.accept("%s!%s ->".formatted(intSet(v), exprGen.compileExpression(init, element)));
      }
    });

    // As in the original module
    final var ctrls = defResolve.controllers(element);
    final var body = composeControllers(ns, element, ctx, ctrls);
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

  private CharSequence composeControllers(String ns, RCModule element, RoboticPlatformDef ctx,
      Stream<Controller> components) {
    final var connections = element.getConnections();

    // The next bits of code use stacks and stack reversing quite a bit, so these comments are
    // examples of what the stack will look like at each stage.

    // pairs = c1, c2, c3, c4, c5
    final var pairs = components.map(x -> renamingControllerChanset(ns, x, connections, ctx))
        .collect(Collectors.toCollection(LinkedList::new));

    // moved one stack onto another, so:
    // ctrls = c5[], c4[c5], c3[c4, c5], c2[c3, c4, c5], c1[c2, c3, c4, c5]
    final var ctrls = makeIntersections(pairs);

    final var cb = csp.bins();
    final var cs = csp.sets();

    // It's ill-formed for there to be no controllers.

    // starting with c5
    // stack has: c4[c5], c3[c4, c5], etc.
    var output = ctrls.pop().body;

    while (!ctrls.isEmpty()) {
      // TODO(@MattWindsor91): implement this more efficiently, eg. as a fold or reduce.
      final var ctrl = ctrls.pop();

      // We don't hide the intersection in this version of the semantics, as sequence diagrams
      // need to be able to inspect inter-controller communications.
      output = cs.tuple(cb.genParallel(ctrl.body,
          cs.enumeratedSet(ctrl.intersection.toArray(CharSequence[]::new)), output));
    }

    return output;
  }

  private LinkedList<Component> makeIntersections(LinkedList<Pair<CharSequence, List<String>>> pairs) {
    final var ctrls = new LinkedList<Component>();
    while (!pairs.isEmpty()) {
      final var x = pairs.pop();

      final var channels = new HashSet<>(x.getValue());
      final var otherChannels = pairs.stream().flatMap(y -> y.getValue().stream())
          .collect(Collectors.toSet());

      ctrls.push(new Component(x.getKey(), Sets.intersection(channels, otherChannels)));
    }
    return ctrls;
  }

  private Pair<CharSequence, List<String>> renamingControllerChanset(String ns, Controller ctrl,
      List<Connection> connections, RoboticPlatformDef rp) {
    final var ctrlDef = defResolve.resolve(ctrl);
    final var ctrlName = csp.namespaced(ns, ctrl.getName());

    final var renaming = csp.renaming();
    final var chanset = new LinkedList<String>();

    renameTerminate(renaming, chanset, ns, ctrlName);
    renameConnections(renaming, chanset, ns, ctrl, connections);

    final var s =
        csp.namespaced(ctrlName, gu.getSuffix(false, true)).toString() + gu.parameterisation(
            ctrlDef, Collections.emptySet());
    final var renamed = renaming.in(s);

    final CharSequence[] unconnectedEvents = unconnectedEvents(ctrl, connections, ctrlDef,
        ctrlName);
    final var hidden = unconnectedEvents.length == 0 ? renamed
        : csp.bins().hide(renamed, csp.enumeratedSet(unconnectedEvents));

    final var body = csp.let(constantDefs(ctrl, rp, ctrlDef)).within(hidden);

    return new Pair<>(body, chanset);
  }

  private CharSequence[] unconnectedEvents(Controller ctrl, List<Connection> connections,
      ControllerDef ctrlDef, CharSequence ctrlName) {
    final var connectedEvents = connections.stream().mapMulti((x, c) -> {
      if (x.getFrom() == ctrl) {
        c.accept(x.getEfrom());
      } else if (x.getTo() == ctrl) {
        c.accept(x.getEto());
      }
    }).collect(Collectors.toSet());

    return gu.allEvents(ctrlDef).stream().filter(e -> !(connectedEvents.contains(e)))
        .map(e -> csp.namespaced(ctrlName, gu.eventId(e))).toArray(CharSequence[]::new);
  }

  private void renameTerminate(Renaming renaming, LinkedList<String> chanset, String ns,
      CharSequence ctrlName) {
    // renamingPairs and chanset is guaranteed to contain at least one event, the termination event
    renaming.rename(terminate(ctrlName), terminate(ns));
    chanset.add(terminate(ns).toString());
  }

  private void renameConnections(Renaming renaming, LinkedList<String> chanset, String ns,
      Controller ctrl, List<Connection> connections) {
    for (var c : connections) {
      final var source = c.getFrom();
      final var target = c.getTo();
      final var esource = c.getEfrom();
      final var etarget = c.getEto();

      // Unlike the RoboChart semantics, we only rename to fuse synchronous connections.  We don't
      // rename connections heading to the robotic platform.
      if (c.isAsync()) {
        continue;
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

  private CharSequence opName(CharSequence ns, OperationSig op) {
    // TODO(@MattWindsor91): deduplicate this in TopicGenerator?
    return csp.namespaced(ns, op.getName() + "Call");
  }

  private CharSequence[] constantDefs(Controller ctrl, RoboticPlatformDef rp,
      ControllerDef ctrlDef) {
    // a required constant in the controller must be defined in the containing
    // robotic platform, therefore it should be defined in instantiations
    // a defined constant is either specified by its initial value, or by a constant in the
    // instantiations file

    // added parameterisation of the controller
    final var consts = gu.allConstants(ctrlDef);
    final var rconsts = gu.requiredConstants(ctrlDef).stream().map(Variable::getName)
        .collect(Collectors.toUnmodifiableSet());

    return consts.stream().mapMulti((Variable k, Consumer<CharSequence> c) -> {
      final var id = gu.constantId(k, ctrlDef);

      if (rconsts.contains(k.getName())) {
        c.accept(csp.definition(id, gu.constantId(k, rp)));
      } else if (k.getInitial() != null) {
        c.accept(csp.definition(id, exprGen.initialValue(k)));
      } else if (ctrl instanceof ControllerRef) {
        c.accept(csp.definition(id, gu.constantId(k, ctrl)));
      }
    }).toArray(CharSequence[]::new);
  }

  /**
   * Stores information about a component for composition purposes.
   *
   * @param body         the body of the component.
   * @param intersection intersection of this component's channels and the others against which it
   *                     is being composed.
   */
  private record Component(CharSequence body, Set<String> intersection) {

  }
}

