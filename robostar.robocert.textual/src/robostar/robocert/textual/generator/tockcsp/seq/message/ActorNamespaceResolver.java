/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.seq.message;

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.Actor;
import robostar.robocert.ModuleTarget;
import robostar.robocert.Target;
import robostar.robocert.TargetActor;
import robostar.robocert.util.ActorNodeResolver;

/**
 * Resolves {@link Actor}s to their CSP namespaces.
 *
 * @param utils        general RoboChart generator utilities.
 * @param nodeResolver actor node resolver.
 * @author Matt Windsor
 */
public record ActorNamespaceResolver(CTimedGeneratorUtils utils, ActorNodeResolver nodeResolver) {

  @Inject
  public ActorNamespaceResolver {
    Objects.requireNonNull(utils);
    Objects.requireNonNull(nodeResolver);
  }

  /**
   * Gets the namespace for all channels attached to the given {@link Actor}.
   *
   * @param actor actor to resolve into a namespace.
   * @return the actor's namespace, as a string.
   */
  public String namespace(Actor actor) {
    return utils.processId(namespaceRoot(actor));
  }

  private EObject namespaceRoot(Actor base) {
    if (base instanceof TargetActor) {
      final var target = nodeResolver.target(base);
      final var root = target.map(this::targetNamespaceRoot)
          .orElseGet(() -> nonTargetNamespaceRoot(base));
      return root.orElseThrow();
    }
    return nonTargetNamespaceRoot(base).orElseThrow();
  }

  private Optional<EObject> targetNamespaceRoot(Target t) {
    // In target actor cases, we want to get the namespace of the target.
    // This differs from the usual code path in one place:
    // if the target is a module, `resolve` would give us the list of components instead of
    // the module, so we do things slightly indirectly.
    // TODO(@MattWindsor91): is that even the right behaviour?
    if (t instanceof ModuleTarget m) {
      return Optional.of(m.getModule());
    }
    return findAnyObject(nodeResolver.resolveTarget(t));
  }

  private Optional<EObject> nonTargetNamespaceRoot(Actor a) {
    return findAnyObject(nodeResolver.resolve(a));
  }

  private static Optional<EObject> findAnyObject(Stream<? extends EObject> objs) {
    // The seemingly redundant map is casting to EObject.
    return objs.findAny().map(x -> x);
  }
}
