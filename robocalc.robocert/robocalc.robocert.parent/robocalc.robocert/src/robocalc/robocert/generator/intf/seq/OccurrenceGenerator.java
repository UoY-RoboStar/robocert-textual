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

import robocalc.robocert.model.robocert.Occurrence;

/**
 * A generator for occurrences.
 *
 * @author Matt Windsor
 */
public interface OccurrenceGenerator {

  /**
   * Generates code for an occurrence from the perspective of a lifeline.
   *
   * @param occ the occurrence.
   * @param ctx context for the current lifeline.
   * @return the generated code.
   */
  CharSequence generate(Occurrence occ, LifelineContext ctx);
}
