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
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.StateMachineBody;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.CollectionTarget;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.ControllerTarget;
import robocalc.robocert.model.robocert.InControllerTarget;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTarget;
import robocalc.robocert.model.robocert.StateMachineTarget;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.World;

/**
 * Resolves actors into the connection nodes that can represent them.
 *
 * @param defResolver helper for resolving parts of the RoboChart object graph.
 */
public record ActorNodeResolver(DefinitionResolver defResolver) {

  /**
   * Constructs an actor resolver.
   *
   * @param defResolver helper for resolving parts of the RoboChart object graph.
   */
  @Inject
  public ActorNodeResolver {
    Objects.requireNonNull(defResolver);
  }

  /**
   * Resolves an actor to a stream of connection nodes that can represent that actor.
   *
   * <p>Compared to {@code resolve}, this handles {@link TargetActor}s connected to
   * {@link ModuleTarget}s in a special way; it returns a singleton stream of the underlying module.
   * This means that the stream is not a stream of {@link ConnectionNode}s.
   *
   * @param actor the actor to resolve.  Must be attached to a specification group.
   * @return a stream of modules or connection nodes that can represent this actor.
   */
  public Stream<? extends EObject> resolveHandlingTargetModules(Actor actor) {
    if (actor instanceof TargetActor) {
      final var ot = target(actor);
      if (ot.isPresent()) {
        final var t = ot.get();
        if (t instanceof ModuleTarget m) {
          return Stream.of(m.getModule());
        }
      }
    }
    return resolve(actor);
  }

  /**
   * Resolves an actor to a stream of connection nodes that can represent that actor.
   *
   * <p>The stream may contain more than one node in two situations: either the actor is a target
   * actor and the target is a module (in which case, the module's non-platform components stand in
   * for the module), or the actor is a world (in which case, any of the parent's connection nodes
   * can appear).
   *
   * @param actor the actor to resolve.  Must be attached to a specification group.
   * @return a stream of connection nodes that can represent this actor.
   */
  public Stream<ConnectionNode> resolve(Actor actor) {
    if (actor instanceof ComponentActor c) {
      return Stream.of(c.getNode());
    }
    return target(actor).stream().flatMap(t -> {
      if (actor instanceof TargetActor) {
        return resolveTarget(t);
      }
      if (actor instanceof World) {
        return resolveWorld(t);
      }
      throw new IllegalArgumentException("can't resolve actor %s".formatted(actor));
    });
  }

  /**
   * Tries to get the target underlying an actor.
   *
   * <p>Well-formed actors should have a target via their attached specification group.
   *
   * @param actor the actor to inspect.
   * @return the target.
   */
  public Optional<Target> target(Actor actor) {
    // TODO(@MattWindsor91): this should be moved somewhere else, but where?
    return Optional.ofNullable(actor.getGroup()).flatMap(g -> Optional.ofNullable(g.getTarget()));
  }

  /**
   * Deduces a stream of connection nodes that can represent the target actor for a target.
   *
   * <p>The stream may contain more than one node if the target is a module (in which case, the
   * module's non-platform components stand in for the module).
   *
   * @param target the target for which we are resolving target-relative actors.
   * @return a stream of connection nodes that can represent the target actor.
   */
  public Stream<ConnectionNode> resolveTarget(Target target) {
    // WFC CGsA2 means that this should not happen in practice.
    if (target instanceof CollectionTarget) {
      return Stream.of();
    }

    // Modules don't have a single connection node, as they are the top-level container for nodes.
    // Instead, we note that everything a module connects to the platform is effectively a
    // surrogate node for the module.
    if (target instanceof ModuleTarget m) {
      return m.getModule().getNodes().stream().filter(x -> !(x instanceof RoboticPlatform));
    }

    if (target instanceof ControllerTarget c) {
      return Stream.of(c.getController());
    }
    if (target instanceof StateMachineTarget s) {
      return Stream.of(s.getStateMachine());
    }
    if (target instanceof OperationTarget o) {
      return Stream.of(o.getOperation());
    }

    throw new IllegalArgumentException("can't resolve actor for target %s".formatted(target));
  }

  /**
   * Deduces a stream of connection nodes that can represent the world actor for a target.
   *
   * <p>The stream may contain more than one node.
   *
   * @param target the target for which we are resolving target-relative actors.
   * @return a stream of connection nodes that can represent the target actor.
   */
  public Stream<ConnectionNode> resolveWorld(Target target) {
    if (target instanceof InModuleTarget m) {
      return moduleWorld(m.getModule());
    }
    if (target instanceof ModuleTarget m) {
      return moduleWorld(m.getModule());
    }

    if (target instanceof InControllerTarget c) {
      return controllerWorld(c.getController());
    }
    if (target instanceof ControllerTarget c) {
      return controllerWorld(c.getController());
    }

    if (target instanceof StateMachineTarget s) {
      return stmBodyWorld(s.getStateMachine());
    }
    if (target instanceof OperationTarget o) {
      return stmBodyWorld(o.getOperation());
    }

    throw new IllegalArgumentException("can't resolve world actor for target %s".formatted(target));
  }

  private Stream<ConnectionNode> moduleWorld(RCModule m) {
    // The world of a module is just its platform (with some casting to ConnectionNode).
    return defResolver.platform(m).stream().map(x -> x);
  }

  private Stream<ConnectionNode> controllerWorld(ControllerDef c) {
    // The world of a controller is everything visible inside its module, except the controller
    // itself.
    return defResolver.module(c).stream().flatMap(m -> {
      final var above = moduleWorld(m);
      final var local = m.getNodes().stream();
      return Stream.concat(above, local.filter(x -> x != c));
    });
  }

  private Stream<ConnectionNode> stmBodyWorld(StateMachineBody s) {
    // The world of a state machine or operation is everything visible inside its controller,
    // except the state machine body itself.
    return defResolver.controller(s).stream().flatMap(c -> {
      final var above = Stream.concat(Stream.of(c), controllerWorld(c));
      final var local = Stream.concat(c.getLOperations().stream(), c.getMachines().stream());
      return Stream.concat(above, local.filter(x -> x != s));
    });
  }
}