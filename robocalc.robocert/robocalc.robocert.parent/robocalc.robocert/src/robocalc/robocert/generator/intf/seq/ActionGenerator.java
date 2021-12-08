/********************************************************************************
 * Copyright (c) 2021 University of York and others
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
package robocalc.robocert.generator.intf.seq;

import robocalc.robocert.model.robocert.SequenceAction;

/**
 * A generator for sequence actions.
 *
 * @author Matt Windsor
 */
public interface ActionGenerator {

  /**
   * Generates code for an action from the perspective of a lifeline..
   *
   * @param a   the action.
   * @param ctx context for the current lifeline.
   * @return the generated code.
   */
  public CharSequence generate(SequenceAction a, LifelineContext ctx);
}
