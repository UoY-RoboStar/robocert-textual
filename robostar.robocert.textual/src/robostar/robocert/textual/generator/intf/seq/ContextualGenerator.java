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

package robostar.robocert.textual.generator.intf.seq;

import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;

/**
 * Interface for things that can generate code for sequence elements given a lifeline context.
 *
 * @param <T> type of input to the generator.
 * @author Matt Windsor
 */
public interface ContextualGenerator<T> {

  /**
   * Generates code for the given input modulo the given context.
   *
   * @param input the input to the generator.
   * @param ctx   the lifeline context.
   * @return code for {@code input} modulo {@code ctx}.
   */
  CharSequence generate(T input, LifelineContext ctx);
}
