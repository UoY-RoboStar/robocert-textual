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

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedModuleGenerator;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.xtext.xbase.lib.Pair;
import robocalc.robocert.generator.tockcsp.ll.csp.Renaming;
import robocalc.robocert.model.robocert.InModuleTarget;

/**
 * Generates bodies of in-module targets.
 *
 * @author Matt Windsor
 */
public class InModuleTargetBodyGenerator extends
    CollectionTargetBodyGenerator<InModuleTarget, RCModule, RoboticPlatformDef> {

  @Inject
  protected CTimedModuleGenerator modGen;

  private boolean isAsyncConnection(Connection c) {
    return c.isAsync() && !(c.getTo() instanceof RoboticPlatform)
        && !(c.getFrom() instanceof RoboticPlatform);
  }

  @Override
  protected RCModule element(InModuleTarget target) {
    return target.getModule();
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

    // TODO(@MattWindsor91): reimplement this
    final var pairs = components.map(x -> renamingControllerChanset(ns, x, connections, ctx))
        .toList();
    final var ctrls = new LinkedList<Component>();
    for (int i = 0; i < pairs.size(); i++) {
      final var x = pairs.get(i);
      final var xs = pairs.subList(i + 1, pairs.size());

      final var channels = new HashSet<>(x.getValue());
      final var otherChannels = xs.stream().flatMap(y -> y.getValue().stream())
          .collect(Collectors.toSet());

      ctrls.add(new Component(x.getKey(), Sets.intersection(channels, otherChannels)));
    }

    final var cb = csp.bins();
    final var cs = csp.sets();

    // It's ill-formed for there to be no controllers.
    var output = ctrls.removeLast().body;
    while (!ctrls.isEmpty()) {
      final var ctrl = ctrls.removeLast();

      final var body =
          cs.tuple(cb.genParallel(ctrl.body,
              cs.enumeratedSet(ctrl.intersection.toArray(CharSequence[]::new)), output));

      final var diff = csp.enumeratedDiff(ctrl.intersection,
          Set.of(csp.namespaced(ns, "terminate").toString()));
      output =  "{}".contentEquals(diff) ? body : cb.hide(body, diff);
    }

    return output;
  }

  private Pair<CharSequence, List<String>> renamingControllerChanset(String ns, Controller ctrl,
      List<Connection> connections, RoboticPlatformDef rp) {
    final var ctrlDef = defResolve.resolve(ctrl);
    final var ctrlName = csp.namespaced(ns, ctrl.getName());

    final var renaming = csp.renaming();
    final var chanset = new LinkedList<String>();

    renameTerminate(renaming, chanset, ns, ctrlName);
    renameConnections(renaming, chanset, ns, ctrl, connections);
    renameVariables(renaming, ns, ctrlDef, ctrlName, rp);
    renameOperations(renaming, ns, ctrlDef, ctrlName, rp);

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

    return gu.allEvents(ctrlDef).stream()
        .filter(e -> !(connectedEvents.contains(e)))
        .map(e -> csp.namespaced(ctrlName, gu.eventId(e))).toArray(CharSequence[]::new);
  }

  private void renameTerminate(Renaming renaming, LinkedList<String> chanset, String ns,
      CharSequence ctrlName) {
    // renamingPairs and chanset is guaranteed to contain at least one event, the termination event
    renaming.rename(terminate(ctrlName), terminate(ns));
    chanset.add(terminate(ns).toString());
  }

  private void renameVariables(Renaming renaming, String ns, ControllerDef ctrlDef,
      CharSequence ctrlName, RoboticPlatformDef rp) {
    // dealing with variable renamings
    // required variables of the controller
    final var rvars = ctrlDef.getRInterfaces().stream().flatMap(i -> i.getVariableList().stream())
        .filter(vl -> vl.getModifier() == VariableModifier.VAR).flatMap(vl -> vl.getVars().stream())
        .map(NamedElement::getName).collect(Collectors.toUnmodifiableSet());

    // variables of the platform (either provided or required) that are also required by the controller
    // matching is done by name, should be updated eventually to match for type as well
    gu.allVariables(rp).stream().filter(v -> rvars.contains(v.getName())).forEach(
        v -> renaming.rename(csp.namespaced(ctrlName, intSet(v)), csp.namespaced(ns, intSet(v)))
            .rename(csp.namespaced(ctrlName, intGet(v)), csp.namespaced(ns, intGet(v))));
  }

  private void renameConnections(Renaming renaming, LinkedList<String> chanset, String ns,
      Controller ctrl, List<Connection> connections) {
    // dealing with connection renamings (only for synchronous connections)
    for (var c : connections) {
      // Here, I should not apply connectionNode to from and to, as they are already connection nodes necessarily. Furthermore,
      // connectionNode skips the reference, and returns the associated definition. We do not want this here as it duplicates
      // connections when multiple references of the same definition are present.
      final var source = c.getFrom();
      final var target = c.getTo();
      final var esource = c.getEfrom();
      final var etarget = c.getEto();
      // the comparison here should be to the original controller (be it a reference or definition)
      if (source instanceof Controller src && src == ctrl) {
        final var oldName = csp.namespaced(ns, gu.ctrlName(src), gu.eventId(esource));

        // if the source is the current controller
        // only rename if the target is the robotic platform
        if (target instanceof RoboticPlatform) {
          renaming.rename(oldName, csp.namespaced(ns, gu.eventId(etarget)));
        } else if (!c.isAsync()) {
          // this event needs to be added because we are using generalised parallelism
          chanset.add(oldName.toString());
        }
      } else if (target instanceof Controller tgt && tgt == ctrl) {
        final var oldName = csp.namespaced(ns, gu.ctrlName(tgt), gu.eventId(etarget)).toString();

        // rename the event if the source if a robotic platform, or if the communication is synchronous
        if (source instanceof RoboticPlatform) {
          // for robotic platform events, just rename the channel, leaving the I/O marking as-is
          renaming.rename(oldName, csp.namespaced(ns, gu.eventId(esource)));
        } else if (source instanceof Controller src && !c.isAsync()) {
          final var newName = csp.namespaced(ns, gu.ctrlName(src), gu.eventId(esource)).toString();

          // for synchronous inter-controller communication, swap in and out to match source
          renaming.rename(oldName + ".in", newName + ".out")
              .rename(oldName + ".out", newName + ".in");
          // if the source is not a robotic platform and the connection is synchronous, add to chanset
          // only add the event to synchronisation set if it is not a controller event
          chanset.add(newName);
        }
      }
      // if neither the source nor the target are
      // the current controller, the connection
      // is irrelevant and there is no renaming
    }
  }

  private void renameOperations(Renaming renaming, String ns, ControllerDef ctrlDef,
      CharSequence ctrlName,
      RoboticPlatformDef rp) {
    // dealing with undefined operations of the controller not defined by the platform

    final var allContextOps = gu.allOperations(rp).stream().map(OperationSig::getName)
        .collect(Collectors.toUnmodifiableSet());

    for (var o : gu.requiredOperations(ctrlDef)) {
      final var name = o.getName();
      if (allContextOps.contains(name)) {
        renaming.rename(opName(ctrlName, o), opName(ns, o));
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

