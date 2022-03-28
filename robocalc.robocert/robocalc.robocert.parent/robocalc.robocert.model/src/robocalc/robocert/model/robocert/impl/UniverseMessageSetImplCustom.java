/*******************************************************************************
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
 ******************************************************************************/
package robocalc.robocert.model.robocert.impl;

/**
 * Adds derived operation definitions to {@link UniverseMessageSetImpl}.
 *
 * @author Matt Windsor
 */
class UniverseMessageSetImplCustom extends UniverseMessageSetImpl {
  /**
   * Universe gap messages sets are always active.
   *
   * @return true.
   */
  @Override
  public boolean isActive() {
    return true;
  }

  /**
   * Universe gap messages sets are always universal.
   *
   * @return true.
   */
  @Override
  public boolean isUniversal() {
    return true;
  }
}
