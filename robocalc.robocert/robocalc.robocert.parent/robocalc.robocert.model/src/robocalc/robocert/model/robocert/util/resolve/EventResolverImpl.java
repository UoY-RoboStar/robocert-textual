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

package robocalc.robocert.model.robocert.util.resolve;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.StateMachineBody;
import circus.robocalc.robochart.StateMachineRef;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.CollectionTarget;
import robocalc.robocert.model.robocert.ComponentActor;
import robocalc.robocert.model.robocert.ComponentTarget;
import robocalc.robocert.model.robocert.ControllerTarget;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.InControllerTarget;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTarget;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.model.robocert.StateMachineTarget;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.World;
import robocalc.robocert.model.robocert.util.ActorNodeResolver;
import robocalc.robocert.model.robocert.util.DefinitionResolver;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public record EventResolverImpl(ActorNodeResolver actorResolver, DefinitionResolver defResolver,
                                RoboCertFactory rcFactory) implements EventResolver {

  @Inject
  public EventResolverImpl {
    Objects.requireNonNull(actorResolver);
    Objects.requireNonNull(defResolver);
  }

  @Override
  public Stream<Connection> resolve(EventTopic topic, Actor from, Actor to) {
    final var target = from.getGroup().getTarget();
    if (target instanceof ComponentTarget t) {
      return resolveComponent(topic, from, to, t);
    }
    if (target instanceof CollectionTarget t) {
      return resolveCollection(topic, from, to, t);
    }
    throw new IllegalArgumentException(
        "target neither component nor collection: %s".formatted(target));
  }

  private Stream<Connection> resolveComponent(EventTopic topic, Actor from, Actor to, Target t) {
    // Component targets are easy to resolve: all of their connections go from the target to
    // the world, or backwards (and so are outbound in some sense).
    return outboundConnections(t).filter(
        x -> matchesComponent(x, topic, actorNodes(from), actorNodes(to)));
  }

  private Stream<Connection> resolveCollection(EventTopic topic, Actor from, Actor to,
      CollectionTarget t) {
    // Collection target connections are more complicated than component target connections, as
    // there are two situations:
    //
    // - from a ComponentActor to another ComponentActor, in which case the connection is inside
    //   the target;
    // - from a ComponentActor to a World, in which case we need to proceed as if we were resolving
    //   a component connection from the target to the world instead.
    if (from instanceof ComponentActor && to instanceof ComponentActor) {
      return innerConnections(t).filter(
          x -> matchesComponent(x, topic, actorNodes(from), actorNodes(to)));
    }

    // WFC CGsA2 has that at least one of these must be the world.
    if (from instanceof World) {
      return resolveOutbound(topic, actorNodes(from), targetNodes(t), t);
    }
    if (to instanceof World) {
      return resolveOutbound(topic, targetNodes(t), actorNodes(to), t);
    }

    throw new IllegalArgumentException(
        "tried to resolve collection with TargetActors - violates CGsA2");
  }

  private Stream<Connection> resolveOutbound(EventTopic topic, Set<ConnectionNode> fromNodes,
      Set<ConnectionNode> toNodes, Target t) {
    return outboundConnections(t).filter(x -> matchesComponent(x, topic, fromNodes, toNodes));
  }


  /**
   * Gets the stream of connections that go from this target to its world.
   *
   * @param target the target whose connections should be enumerated.
   * @return the stream of outbound connections.
   */
  private Stream<Connection> outboundConnections(Target target) {
    // We consider the connections from module elements to the platform to be 'outer', here.
    if (target instanceof InModuleTarget m) {
      return outboundModuleConnections(m.getModule());
    }
    if (target instanceof ModuleTarget m) {
      return outboundModuleConnections(m.getModule());
    }

    if (target instanceof InControllerTarget c) {
      return outboundControllerConnections(c.getController());
    }
    if (target instanceof ControllerTarget c) {
      return outboundControllerConnections(c.getController());
    }

    if (target instanceof StateMachineTarget s) {
      return outboundStateMachineBodyConnections(s.getStateMachine());
    }
    if (target instanceof OperationTarget o) {
      return outboundStateMachineBodyConnections(o.getOperation());
    }

    throw new IllegalArgumentException(
        "can't get outbound connections for target %s".formatted(target));
  }

  private Set<ConnectionNode> actorNodes(Actor from) {
    return actorResolver.resolve(from).collect(Collectors.toUnmodifiableSet());
  }

  private Set<ConnectionNode> targetNodes(Target t) {
    return actorResolver.resolveTarget(t).collect(Collectors.toUnmodifiableSet());
  }

  private boolean matchesComponent(Connection c, EventTopic topic, Set<ConnectionNode> from,
      Set<ConnectionNode> to) {
    if (!(nodesMatch(c, from, to) || (c.isBidirec() && nodesMatch(c, to, from)))) {
      return false;
    }
    // TODO(@MattWindsor91): do we need reversibility here?
    if (!EcoreUtil2.equals(topic.getEfrom(), c.getEfrom())) {
      return false;
    }
    final var eto = topic.getEto();
    return eto == null || EcoreUtil2.equals(topic.getEto(), c.getEto());
  }

  private boolean nodesMatch(Connection c, Set<ConnectionNode> from, Set<ConnectionNode> to) {
    return from.contains(c.getFrom()) && to.contains(c.getTo());
  }

  /**
   * Gets the connections between components inside this target.
   *
   * @param target the collection target whose connections we are searching.
   * @return the stream of connections defined between this target's components.
   */
  private Stream<Connection> innerConnections(CollectionTarget target) {
    if (target instanceof InModuleTarget m) {
      return moduleConnections(m.getModule()).filter(x -> !connectsPlatform(x));
    }
    if (target instanceof InControllerTarget c) {
      return c.getController().getConnections().stream();
    }

    throw new IllegalArgumentException("can't get inner connections of %s".formatted(target));
  }

  //
  // Utilities
  //

  private Stream<Connection> moduleConnections(RCModule m) {
    return m.getConnections().stream();
  }

  private Stream<Connection> controllerConnections(ControllerDef ctrl) {
    return ctrl.getConnections().stream();
  }

  private Stream<Connection> outboundModuleConnections(RCModule m) {
    return moduleConnections(m).filter(this::connectsPlatform);
  }

  private Stream<Connection> outboundControllerConnections(ControllerDef ctrl) {
    // An outbound controller connection is any connection in the module that goes to or from the
    // controller.
    return defResolver.module(ctrl).stream().flatMap(this::moduleConnections)
        .filter(c -> connectsController(c, ctrl));
  }

  private Stream<Connection> outboundStateMachineBodyConnections(StateMachineBody smb) {
    return defResolver.controller(smb).stream().flatMap(this::controllerConnections)
        .filter(c -> connectsStateMachine(c, smb));
  }

  private boolean connectsController(Connection c, ControllerDef ctrl) {
    return connectsController(c.getFrom(), ctrl) || connectsController(c.getTo(), ctrl);
  }

  private boolean connectsController(ConnectionNode n, ControllerDef ctrl) {
    if (n instanceof ControllerRef r) {
      n = r.getRef();
    }
    return n == ctrl;
  }

  private boolean connectsStateMachine(Connection c, StateMachineBody smb) {
    return connectsStateMachine(c.getFrom(), smb) || connectsStateMachine(c.getTo(), smb);
  }

  private boolean connectsStateMachine(ConnectionNode n, StateMachineBody smb) {
    if (n instanceof StateMachineRef r) {
      n = r.getRef();
    } else if (n instanceof OperationRef r) {
      n = r.getRef();
    }

    return n == smb;
  }

  private boolean connectsPlatform(Connection c) {
    return c.getFrom() instanceof RoboticPlatform || c.getTo() instanceof RoboticPlatform;
  }
}
