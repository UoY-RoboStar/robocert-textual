/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.core.tgt;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import circus.robocalc.robochart.generator.csp.comp.untimed.CMemoryGenerator;
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator;
import robostar.robocert.textual.generator.tockcsp.core.tgt.ComponentSynchroniser.Result;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.util.resolve.DefinitionResolver;

/**
 * Generates CSP-M for the bodies of collection targets.
 * <p>
 * Unlike component targets (for which we can just use the processes generated by the RoboChart
 * semantics), collection targets have to pry into what would usually be black-box definitions at
 * the RoboChart level.  This means we need to replicate the insides of those definitions here; as
 * such, this generator is heavily based on the generator used for RoboChart module and controller
 * processes.
 *
 * @param <E> type of target elements.
 * @param <C> type of contexts used for memory etc.
 * @param <T> type of subcomponents.
 * @author Alvaro Miyazawa (initial definition in RoboChart)
 * @author Pedro Ribeiro (initial definition in RoboChart)
 * @author Matt Windsor (port to RoboCert)
 */
public abstract class CollectionTargetBodyGenerator<E extends EObject, C extends Context, T extends ConnectionNode> {

  @Inject
  protected DefinitionResolver defRes;
  @Inject
  protected CSPStructureGenerator csp;
  @Inject
  protected CTimedGeneratorUtils gu;
  @Inject
  protected CMemoryGenerator memGen;
  @Inject
  protected ExpressionGenerator exprGen;
  @Inject
  private TerminationGenerator termGen;

  /**
   * Generates CSP-M for a collection target.
   *
   * @param element the element of the target to generate.
   * @return CSP-M for the target definition.
   */
  public CharSequence generate(E element) {
    final var ctx = context(element);
    final var ns = namespace(element);
    final var comps = components(element);

    final var composed = composeComponents(ns, element, ctx, comps);
    final var innerBody = wrapInner(element, ctx, composed);

    final var body = addMemory(ns, ctx, comps, innerBody);
    return handleTerminationAndOptimise(ns, wrapOuter(element, ctx, body));
  }

  /**
   * Gets the namespace of the target's element.
   *
   * @param element the element in question.
   * @return the target's element's namespace.
   */
  protected abstract String namespace(E element);

  /**
   * Gets the memory context of the target's element.
   *
   * @param element the element in question.
   * @return the target's element's memory context (itself for controllers, the platform for
   * modules).
   */
  protected abstract C context(E element);

  /**
   * Gets the components of the target's element.
   *
   * @param element the element in question.
   * @return the subcomponents of the target element (STMs for controllers, controllers for
   * modules).
   */
  protected abstract List<T> components(E element);

  /**
   * Gets the connections of the target's element.
   *
   * @param element the element in question.
   * @return the connections of the target element.
   */
  protected abstract List<Connection> connections(E element);

  /**
   * Resolves a subcomponent to its definition.
   *
   * @param comp the subcomponent in question.
   * @return the definition, as a context.
   */
  protected abstract Context definition(T comp);

  /**
   * Resolves a subcomponent to its name.
   *
   * @param comp the subcomponent in question.
   * @return the name.
   */
  protected abstract String name(T comp);

  /**
   * @return the class of {@code T}.
   */
  protected abstract Class<T> compClass();

  /**
   * Wraps the inner body of the target.
   *
   * @param element element of the target being generated.
   * @param ctx     memory context of the target being generated.
   * @param body    the outer body (eg, before parallelising with the memory).
   * @return CSP-M for the wrapped body.
   */
  protected abstract CharSequence wrapInner(E element, C ctx, CharSequence body);

  /**
   * Wraps the outer body of the target.
   *
   * @param element element of the target being generated.
   * @param ctx     memory context of the target being generated.
   * @param body    the outer body (eg, after parallelising with the memory).
   * @return CSP-M for the wrapped body.
   */
  protected abstract CharSequence wrapOuter(E element, C ctx, CharSequence body);

  private CharSequence composeComponents(String ns, E element, C ctx, List<T> components) {

    // The next bits of code use stacks and stack reversing quite a bit, so these comments are
    // examples of what the stack will look like at each stage.

    final var conns = connections(element);
    final var syncConns = conns.stream().filter(c -> !c.isAsync()).toList();

    final var cs = new ComponentSynchroniser<>(csp, ns, this::name, compClass());

    // pairs = c1, c2, c3, c4, c5
    final var syncs = components.stream().map(x -> cs.calculate(x, syncConns))
        .collect(Collectors.toCollection(ArrayDeque::new));

    // moved one stack onto another, so:
    // ctrls = c5[], c4[c5], c3[c4, c5], c2[c3, c4, c5], c1[c2, c3, c4, c5]
    final var ctrls = expandComponents(syncs, conns, ctx);

    final var cb = csp.bins();
    final var sets = csp.sets();

    // It's ill-formed for there to be no controllers.
    assert (!ctrls.isEmpty());

    // starting with c5
    // stack has: c4[c5], c3[c4, c5], etc.
    var output = ctrls.pop().body;

    while (!ctrls.isEmpty()) {
      // TODO(@MattWindsor91): implement this more efficiently, eg. as a fold or reduce.
      final var ctrl = ctrls.pop();

      // We don't hide the intersection in this version of the semantics, as sequence diagrams
      // need to be able to inspect inter-controller communications.
      output = sets.tuple(cb.genParallel(ctrl.body,
          sets.enumeratedSet(ctrl.intersection.toArray(CharSequence[]::new)), output));
    }

    return output;
  }

  private Deque<Component> expandComponents(Deque<Result<T>> syncs, List<Connection> conns, C ctx) {
    final var ctrls = new ArrayDeque<Component>();
    while (!syncs.isEmpty()) {
      final var x = syncs.pop();
      ctrls.push(expandComponent(syncs, conns, ctx, x));
    }
    return ctrls;
  }

  private Component expandComponent(Deque<Result<T>> syncs, List<Connection> conns, C ctx,
      Result<T> x) {
    final var comp = x.comp();
    final var compDef = definition(comp);

    final var compName = x.name();
    final var fullName = csp.namespaced(compName, gu.getSuffix(false, true)).toString();
    final var mainBody = fullName + gu.parameterisation(compDef, Collections.emptySet());
    final var renamed = x.renaming().in(mainBody);

    final CharSequence[] unconnectedEvents = unconnectedEvents(comp, conns, compDef, compName);
    final var hidden = unconnectedEvents.length == 0 ? renamed
        : csp.bins().hide(renamed, csp.enumeratedSet(unconnectedEvents));

    final var body = csp.let(constantDefs(comp, ctx, compDef)).within(hidden);

    final var otherChannels = syncs.stream().flatMap(y -> y.channels().stream())
        .collect(Collectors.toUnmodifiableSet());

    return new Component(body, Sets.intersection(x.channels(), otherChannels));
  }

  private CharSequence[] unconnectedEvents(ConnectionNode comp, List<Connection> conns,
      Context compDef, CharSequence compName) {
    final var connectedEvents = conns.stream().mapMulti((x, c) -> {
      if (x.getFrom() == comp) {
        c.accept(x.getEfrom());
      } else if (x.getTo() == comp) {
        c.accept(x.getEto());
      }
    }).collect(Collectors.toSet());

    return gu.allEvents(compDef).stream().filter(e -> !(connectedEvents.contains(e)))
        .map(e -> csp.namespaced(compName, e.getName())).toArray(CharSequence[]::new);
  }

  protected String intSet(Variable v) {
    return "set_" + gu.variableId(v);
  }

  protected String extSet(Variable v) {
    return "set_EXT_" + gu.variableId(v);
  }

  private CharSequence addMemory(String ns, C ctx, List<T> comps, CharSequence innerBody) {
    final var locals = gu.allLocalVariables(ctx).stream().map(this::intSet);
    final var compVars = comps.stream().flatMap(this::componentVars);
    final var memorySet = Stream.concat(locals, compVars).map(v -> csp.namespaced(ns, v))
        .toArray(CharSequence[]::new);
    // Don't add a memory if there is no need for one.
    if (memorySet.length == 0) {
      return innerBody;
    }

    // Assuming always optimised, eg adding dbisim.
    final var mem = csp.function("dbisim",
        csp.namespaced(ns, "Memory") + memGen.memoryInstantiation(ctx));

    return csp.bins().genParallel(innerBody, csp.enumeratedSet(memorySet), mem);
  }

  private Stream<CharSequence> componentVars(T element) {
    return gu.requiredVariables(definition(element)).stream()
        .map(v -> csp.namespaced(name(element), extSet(v)));
  }

  private CharSequence handleTerminationAndOptimise(CharSequence ns, CharSequence body) {
    // nb: this differs from the usual semantics of controllers - where termination is propagated
    // up to the module level - but is in accordance with the usual semantics of modules.
    final var terminated = termGen.handleTerminate(ns, body);

    final var visibleMemoryEvents = csp.namespaced(ns, "visibleMemoryEvents");
    return csp.function("sbisim",
        csp.prioritise(terminated, visibleMemoryEvents, csp.sets().tock()));
  }

  protected CharSequence[] constantDefs(T ctrl, Context parentDef, Context compDef) {
    // a required constant in the controller must be defined in the containing
    // robotic platform, therefore it should be defined in instantiations
    // a defined constant is either specified by its initial value, or by a constant in the
    // instantiations file

    // added parameterisation of the controller
    final var consts = gu.allConstants(compDef);
    final var rconsts = gu.requiredConstants(compDef).stream().map(Variable::getName)
        .collect(Collectors.toUnmodifiableSet());

    return consts.stream().mapMulti((Variable k, Consumer<CharSequence> c) -> {
      final var id = gu.constantId(k, compDef);

      if (rconsts.contains(k.getName())) {
        c.accept(csp.definition(id, gu.constantId(k, parentDef)));
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
   * @param body         body of the component.
   * @param intersection intersection of this component's channels and the others against which it
   *                     is being composed.
   */
  private record Component(CharSequence body, Set<String> intersection) {

    /**
     * Constructs a component.
     *
     * @param body         body of the component.
     * @param intersection intersection of this component's channels and the others against which it
     *                     is being composed.
     */
    public Component {
      Objects.requireNonNull(body);
      Objects.requireNonNull(intersection);
    }
  }
}
