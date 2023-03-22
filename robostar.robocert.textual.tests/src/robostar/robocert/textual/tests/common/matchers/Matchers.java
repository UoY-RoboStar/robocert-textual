/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.common.matchers;

import org.eclipse.emf.ecore.EObject;

/**
 * Hamcrest matchers for RoboCert tests.
 *
 * @author Matt Windsor
 */
public class Matchers {

  /**
   * Constructs a has-scope matcher with the given expected objects.
   *
   * @param expected the objects to test against.
   */
  public static HasScope hasScope(EObject...expected) {
    return new HasScope(expected);
  }
}
