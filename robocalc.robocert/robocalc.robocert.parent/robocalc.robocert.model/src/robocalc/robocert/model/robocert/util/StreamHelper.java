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
package robocalc.robocert.model.robocert.util;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

/**
 * Miscellaneous stream helpers.
 *
 * @author Matt Windsor
 */
public class StreamHelper {

  /**
   * Produces a stream by concatenating a single item onto another stream.
   *
   * @param head the head of the concatenated stream.
   * @param tail the stream to which we are concatenating.
   * @param <T>  type of elements in the string.
   * @return the concatenation of head and tail.
   */
  public static <T> Stream<T> push(T head, Stream<? extends T> tail) {
    return Stream.concat(Stream.of(head), tail);
  }

  /**
   * Filters a stream based on type.
   *
   * @param in    the input stream.
   * @param clazz class of the output type.
   * @param <T>   the input type.
   * @param <U>   the output type.
   * @return the input stream, filtered only to those items of type U.
   */
  public static <T, U extends T> Stream<U> filter(Stream<T> in, Class<U> clazz) {
    return in.filter(clazz::isInstance).map(clazz::cast);
  }

  /**
   * Gets the first item, if any, of a stream that has the given type.
   *
   * @param <T>   the input type.
   * @param <U>   the output type.
   * @param in    the input stream.
   * @param clazz class of the output type.
   * @return the first item, if any, of the input stream that is an instance of type U.
   */
  public static <T, U extends T> Optional<U> firstOfClass(Stream<T> in, Class<U> clazz) {
    return filter(in, clazz).findFirst();
  }

  /**
   * Collects a stream to an EList, potentially downcasting.
   *
   * @param in  the stream to collect.
   * @param <T> the output type.
   * @param <U> the input type.
   * @return the EList containing the contents of in.
   */
  public static <T, U extends T> EList<T> toEList(Stream<U> in) {
    return in.collect(Collectors.toCollection(BasicEList::new));
  }
}
