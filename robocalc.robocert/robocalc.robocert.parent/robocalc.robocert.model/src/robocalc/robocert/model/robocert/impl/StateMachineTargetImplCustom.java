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

package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.NamedElement;

/**
 * Adds derived operation definitions to {@link StateMachineTargetImpl}.
 *
 * @author Matt Windsor
 */
public class StateMachineTargetImplCustom extends StateMachineTargetImpl {
  @Override
  public NamedElement basicGetElement() {
    return getStateMachine();
  }

  /**
   * @return a human-readable summary of this controller.
   */
  @Override
  public String toString() {
    return "state machine " + getStateMachine().getName();
  }
}
