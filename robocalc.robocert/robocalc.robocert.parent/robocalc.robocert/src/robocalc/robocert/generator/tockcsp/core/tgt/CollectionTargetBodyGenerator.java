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
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import circus.robocalc.robochart.generator.csp.comp.untimed.CMemoryGenerator;
import circus.robocalc.robochart.generator.csp.untimed.ExpressionGenerator;
import com.google.inject.Inject;
import java.util.List;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.util.DefinitionResolver;

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
 * @author Matt Windsor
 */
public abstract class CollectionTargetBodyGenerator<E extends EObject, C extends Context, T extends EObject> {

  @Inject
  protected CSPStructureGenerator csp;
  @Inject
  protected DefinitionResolver defResolve;
  @Inject
  protected CTimedGeneratorUtils gu;
  @Inject
  protected CMemoryGenerator memGen;
  @Inject
  protected ExpressionGenerator exprGen;

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

    final var body = addMemory(ns, ctx, comps, innerBody(ns, element, ctx));
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
   * Gets the variables of one of the target's component.
   *
   * @param component component of the target being generated.
   * @return a stream of generated references to the elements of components of the element.
   */
  protected abstract Stream<CharSequence> componentVars(T component);

  /**
   * Creates the inner body of the target.
   *
   * @param ns      namespace of the target being generated.
   * @param element element of the target being generated.
   * @param ctx     memory context of the target being generated.
   * @return CSP-M for the wrapped body.
   */
  protected abstract CharSequence innerBody(String ns, E element, C ctx);

  /**
   * Wraps the outer body of the target.
   *
   * @param element element of the target being generated.
   * @param ctx     memory context of the target being generated.
   * @param body    the outer body (eg, after parallelising with the memory).
   * @return CSP-M for the wrapped body.
   */
  protected abstract CharSequence wrapOuter(E element, C ctx, CharSequence body);

  /**
   * Constructs a reference to the termination channel.
   *
   * @param ns the namespace of the target element.
   * @return the terminate channel (not in a set).
   */
  protected CharSequence terminate(CharSequence ns) {
    return csp.namespaced(ns, "terminate");
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
    final var memorySet = Stream.concat(locals, compVars)
        .map(v -> csp.namespaced(ns, v)).toArray(CharSequence[]::new);
    // Don't add a memory if there is no need for one.
    if (memorySet.length == 0) {
      return innerBody;
    }

    // Assuming always optimised, eg adding dbisim.
    final var mem = csp.function("dbisim",
        csp.namespaced(ns, "Memory") + memGen.memoryInstantiation(ctx));

    return csp.bins().genParallel(innerBody, csp.enumeratedSet(memorySet), mem);
  }

  private CharSequence handleTerminationAndOptimise(CharSequence ns, CharSequence body) {
    final var cs = csp.sets();
    final var cb = csp.bins();

    final var terminate = cs.set(terminate(ns));
    final var terminated = cb.interrupt(csp.tuple(body), terminate, csp.skip());
    final var hidden = cb.hide(terminated, terminate);

    final var priorities = cs.list(csp.namespaced(ns, "visibleMemoryEvents"), cs.set("tock"));
    final var prioritised = csp.function("prioritise", hidden, priorities);
    return csp.function("sbisim", prioritised);
  }
}
