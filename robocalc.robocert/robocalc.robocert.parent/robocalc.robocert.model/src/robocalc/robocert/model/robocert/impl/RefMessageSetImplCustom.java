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

import robocalc.robocert.model.robocert.MessageSet;

/**
 * Adds derived operation definitions to {@link RefMessageSetImpl}.
 *
 * @author Matt Windsor
 */
public class RefMessageSetImplCustom extends RefMessageSetImpl {
  /**
   * Ref gap messages sets are active if their referred-to set is.
   *
   * @return an optimistic estimate of whether there is at least one message in the set.
   */
  @Override
  public boolean isActive() {
    final var s = deref();
    return s != null && s.isActive();
  }

  /**
   * Ref gap messages sets are universal if their referred-to set is.
   *
   * @return a pessimistic estimate of whether every message is in the set.
   */
  @Override
  public boolean isUniversal() {
    final var s = deref();
    return s != null && s.isUniversal();
  }

  private MessageSet deref() {
    final var ns = getSet();
    return (ns == null) ? null : ns.getSet();
  }
}
