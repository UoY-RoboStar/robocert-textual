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

import java.util.stream.Stream;

/**
 * Miscellaneous stream helpers.
 *
 * @author Matt Windsor
 */
public class StreamHelpers {
	/**
	 * Filters a stream based on type.
	 * @param <T> the input type.
	 * @param <U> the output type.
	 * @param in the input stream.
	 * @param clazz class of the output type.
	 * @return the input stream, filtered only to those items of type U.
	 */
	public static <T, U extends T> Stream<U> filter(Stream<T> in, Class<U> clazz) {
		return in.filter(clazz::isInstance).map(clazz::cast);
	}
}
