/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.intf.seq;

import robostar.robocert.Occurrence;

/**
 * A generator for occurrences.
 *
 * @author Matt Windsor
 */
public interface OccurrenceGenerator {

  /**
   * Generates code for an occurrence.
   *
   * <p>This generator does not handle lifeline context specifics; the occurrence fragment
   * generator will handle them.
   *
   * @param occ the occurrence.
   * @return the generated code.
   */
  CharSequence generate(Occurrence occ);
}
