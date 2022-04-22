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
 *   $author - initial definition
 ******************************************************************************/

package robocalc.robocert.model.robocert.util.resolve;

/**
 * Functional interface for things that resolve names.
 *
 * @param <T> type of elements to name.
 */
public interface NameResolver<T> {

  /**
   * Gets the name of the element.
   *
   * @param element the element itself.
   * @return the name of the element, as an array of segments to be conjoined with (eg) the CSP
   * namespacing operator.
   */
  String[] name(T element);
}
