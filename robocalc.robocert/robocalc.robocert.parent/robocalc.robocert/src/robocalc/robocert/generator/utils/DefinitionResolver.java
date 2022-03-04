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

package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.Operation;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.StateMachine;
import circus.robocalc.robochart.StateMachineDef;
import circus.robocalc.robochart.StateMachineRef;

/**
 * Resolves things that can be either definitions or references into definitions.
 *
 * @author Matt Windsor
 */
public class DefinitionResolver {
  /**
   * Resolves an {@link Operation} into an {@link OperationDef}.
   * @param op the operation to resolve.
   * @return the resolved operation.
   */
  public OperationDef resolve(Operation op) {
    if (op instanceof OperationDef d) {
      return d;
    }
    if (op instanceof OperationRef r) {
      return r.getRef();
    }
    throw new IllegalArgumentException("expected OperationDef or OperationRef, got %s".formatted(op));
  }

  /**
   * Resolves a {@link StateMachine} into an {@link StateMachineDef}.
   * @param stm the state machine to resolve.
   * @return the resolved state machine.
   */
  public StateMachineDef resolve(StateMachine stm) {
    if (stm instanceof StateMachineDef d) {
      return d;
    }
    if (stm instanceof StateMachineRef r) {
      return r.getRef();
    }
    throw new IllegalArgumentException("expected StateMachineDef or StateMachineRef, got %s".formatted(stm));
  }
}
