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
 * Adds derived operation definitions to {@link ExtensionalMessageSetImpl}.
 *
 * @author Matt Windsor
 */
class ExtensionalMessageSetImplCustom extends ExtensionalMessageSetImpl {
  /**
   * Extensional gap messages sets are active if non-empty.
   *
   * @return whether there is at least one message in the set.
   */
  @Override
  public boolean isActive() {
    final var msgs = getMessages();
    return !(msgs == null || msgs.isEmpty());
  }

  /**
   * Extensional gap messages sets are never universal.
   *
   * <p>Technically, they could be if they enumerated every possible message in the context, but
   * isUniversal is allowed to be pessimistic and so we don't check that.
   *
   * @return false.
   */
  @Override
  public boolean isUniversal() {
    return false;
  }
}
