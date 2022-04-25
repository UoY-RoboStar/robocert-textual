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

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.util.StreamHelper;

/**
 * Resolves various aspects of modules.
 *
 * @param defRes utility for resolving references into definitions.
 * @author Matt Windsor
 */
public record ModuleResolver(DefinitionResolver defRes) implements NameResolver<RCModule> {

  /**
   * Constructs a module resolver.
   *
   * @param defRes utility for resolving references into definitions.
   */
  @Inject
  public ModuleResolver {
    Objects.requireNonNull(defRes);
  }

  @Override
  public String[] name(RCModule element) {
    // RCModules are at the top of their namespace.
    return new String[]{element.getName()};
  }

  /**
   * Gets the robotic platform definition for a RoboChart module.
   *
   * @param it the RoboChart module.
   * @return the module's robotic platform, if it has one.
   */
  public Optional<RoboticPlatformDef> platform(RCModule it) {
    return nodes(it, RoboticPlatform.class).map(defRes::resolve).findFirst();
  }

  /**
   * Gets the controller definitions for a RoboChart module.
   *
   * @param it the RoboChart module.
   * @return the module's controllers.
   */
  public Stream<Controller> controllers(RCModule it) {
    return nodes(it, Controller.class);
  }

  private <T extends ConnectionNode> Stream<T> nodes(RCModule m, Class<T> clazz) {
    if (m == null) {
      return Stream.empty();
    }
    return StreamHelper.filter(m.getNodes().parallelStream(), clazz);
  }
}
