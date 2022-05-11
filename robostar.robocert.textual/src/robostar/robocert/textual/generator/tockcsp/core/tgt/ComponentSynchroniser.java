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

package robostar.robocert.textual.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Event;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.Renaming;

/**
 * Calculates the renamings and synchronisation needed to coordinate the sync connections of
 * components when generating a collection target body.
 *
 * @param csp       the CSP structure generator.
 * @param namespace the root namespace of the collection target's element.
 * @param compNamer function for naming components (must include the root namespace).
 * @param clazz     used to cast connection nodes into the expected type.
 * @param <T>       type of components.
 */
public record ComponentSynchroniser<T>(CSPStructureGenerator csp, String namespace,
                                       Function<T, String> compNamer, Class<T> clazz) {

  /**
   * Performs the calculation.
   *
   * @param comp      the component to rename.
   * @param syncConns a list of synchronous connections within the parent of the component. These
   *                  must have been pre-filtered for synchrony; exceptions will be thrown
   *                  otherwise.
   * @return the result of the calculation, including the renaming and the synchronising set.
   */
  public Result<T> calculate(T comp, List<Connection> syncConns) {
    final var name = csp.namespaced(namespace, compNamer.apply(comp)).toString();
    final var r = new Result<>(comp, name, csp.renaming(), new HashSet<>());

    renameTerminate(r.renaming, name);
    renameConnections(r.renaming, comp, syncConns);

    syncOnTerminate(r.channels);
    syncOnConnections(r.channels, comp, syncConns);

    return r;
  }

  private void renameTerminate(Renaming r, String name) {
    r.rename(terminate(name), terminate(namespace));
  }

  private void syncOnTerminate(Set<String> channels) {
    channels.add(terminate(namespace).toString());
  }

  private void renameConnections(Renaming r, T comp, List<Connection> conns) {
    conns.stream().filter(c -> shouldRename(comp, c)).forEach(c -> renameConnection(r, c));
  }

  private boolean shouldRename(T comp, Connection conn) {
    // the caller should've handled this
    if (conn.isAsync()) {
      throw new IllegalArgumentException("should not rename async connections");
    }

    // Unlike the RoboChart semantics, we only rename to fuse synchronous connections.
    // This renamer is therefore significantly cut down from the original CModuleGenerator and
    // CControllerGenerator equivalents (though could be extended to do the same job, if needed).

    // Fusion renames happen if we are looking at the target of a connection, and the connection
    // goes to the same type of component.
    return comp == conn.getTo() && clazz.isInstance(conn.getFrom());
  }

  private void renameConnection(Renaming r, Connection c) {
    final var srcName = fullEventName(c.getFrom(), c.getEfrom());
    final var tgtName = fullEventName(c.getTo(), c.getEto());

    r.rename(tgtName + ".in", srcName + ".out").rename(tgtName + ".out", srcName + ".in");
  }

  private void syncOnConnections(Set<String> channels, T comp, List<Connection> conns) {
    final var cs = conns.stream().filter(c -> shouldSync(comp, c))
        .map(c -> fullEventName(c.getFrom(), c.getEfrom())).collect(Collectors.toUnmodifiableSet());

    channels.addAll(cs);
  }

  private boolean shouldSync(T comp, Connection conn) {
    // the caller should've handled this
    if (conn.isAsync()) {
      throw new IllegalArgumentException("should not rename async connections");
    }

    // We add a channel to the synchronisation set if either end of it involves this component,
    // and it goes to a component of the same type.
    final var from = conn.getFrom();
    final var to = conn.getTo();
    return (comp == from && clazz.isInstance(to)) || (comp == to && clazz.isInstance(from));
  }

  private String fullEventName(ConnectionNode comp, Event evt) {
    // Assuming that something else has validated this cast.
    final var c = clazz.cast(comp);
    return csp.namespaced(namespace, compNamer.apply(c), evt.getName()).toString();
  }

  /**
   * Constructs a reference to the termination channel.
   *
   * @param ns the namespace of the target element.
   * @return the terminate channel (not in a set).
   */
  private CharSequence terminate(CharSequence ns) {
    // TODO(@MattWindsor91): duplicated with CollectionTargetBodyGenerator
    return csp.namespaced(ns, "terminate");
  }

  /**
   * Result of calculating the synchronisation for a component.
   *
   * @param comp     the component.
   * @param name     the fully qualified name of the component.
   * @param renaming the CSP renaming operation being built.
   * @param channels the list of channels that will need to be synchronised when this component is
   *                 composed.
   */
  public record Result<T>(T comp, String name, Renaming renaming, Set<String> channels) {

  }
}
