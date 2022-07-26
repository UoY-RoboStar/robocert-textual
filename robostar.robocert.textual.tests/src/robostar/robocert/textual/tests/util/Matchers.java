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

package robostar.robocert.textual.tests.util;

import org.eclipse.emf.ecore.EObject;
import robostar.robocert.textual.tests.util.matchers.HasScope;

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
