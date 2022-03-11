/********************************************************************************
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
 ********************************************************************************/
package robocalc.robocert.model.robocert.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Variable;
import robocalc.robocert.model.robocert.ConstAssignment;

/**
 * Helpers for dealing with an instantiation (list of ConstAssignments).
 */
public class InstantiationHelper {
	/**
	 * Gets a stream of all constants instantiated by this instantiation.
	 *
	 * This stream is not deduplicated; we assume that either the
	 * instantiation is well-formed (no multiple assignments), or that any
	 * deduplication happens later on.
	 * 
	 * @param inst the instantiation to inspect (may be null).
	 * @return the stream of constants in inst.
	 */
	public Stream<Variable> allConstants(List<ConstAssignment> inst) {
		return nullableInstToStream(inst)
				.flatMap(x -> x.getConstants().stream());
	}
	
	/**
	 * Tries to get a constant from an instantiation.
	 *
	 * @param inst the instantiation to inspect (may be null).
	 * @param k the constant to find.
	 * @return the expression corresponding to k, if it is instantiated in inst.
	 */
	public Optional<Expression> getConstant(List<ConstAssignment> inst, Variable k) {
		return nullableInstToStream(inst)
				.mapMulti(
						(ConstAssignment x, Consumer<Expression> acc) -> {
							if (x.hasConstant(k)) {
								acc.accept(x.getValue());
							}
						})
				.findFirst();
	  }
	
	private Stream<ConstAssignment> nullableInstToStream(List<ConstAssignment> inst) {
		return Optional.ofNullable(inst).stream().flatMap(List::stream);
	}
}
