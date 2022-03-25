/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.NamedElement;

/**
 * Adds derived operation definitions to {@link ModuleTargetImpl}.
 *
 * @author Matt Windsor
 */
public class ModuleTargetImplCustom extends ModuleTargetImpl {
  @Override
  public NamedElement basicGetElement() {
    return getModule();
  }

  /**
   * @return a human-readable summary of this module.
   */
  @Override
  public String toString() {
    return "module " + getModule().getName();
  }
}
