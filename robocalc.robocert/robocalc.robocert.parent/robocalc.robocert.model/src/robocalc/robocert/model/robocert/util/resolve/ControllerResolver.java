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

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import java.util.Optional;
import org.eclipse.xtext.EcoreUtil2;

/**
 * Resolves various aspects of controllers.
 *
 * @author Matt Windsor
 */
public class ControllerResolver implements NameResolver<ControllerDef> {

  /**
   * Gets the enclosing module for a RoboChart controller.
   * <p>
   * This assumes that the controller is inside a module.
   *
   * @param c the RoboChart controller.
   * @return the controller's module, if it has one.
   */
  public Optional<RCModule> module(ControllerDef c) {
    return Optional.ofNullable(EcoreUtil2.getContainerOfType(c, RCModule.class));
  }

  @Override
  public String[] name(ControllerDef element) {
    final var base = element.getName();
    return module(element).map(m -> new String[]{m.getName(), base}).orElse(new String[]{base});
  }
}
