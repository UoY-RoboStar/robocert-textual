/*******************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
package robocalc.robocert.model.robocert.impl;

/**
 * Implements derived functionality for {@link BinaryMessageSetImpl}.
 *
 * @author Matt Windsor
 */
public class BinaryMessageSetImplCustom extends BinaryMessageSetImpl {
	/**
	 * @return an optimistic assumption as to whether this message set has any
	 *         messages.
	 */
	@Override
	public boolean isActive() {
		return switch (operator) {
			case UNION -> lhs.isActive() || rhs.isActive();
			case INTERSECTION -> lhs.isActive() && rhs.isActive();
			case DIFFERENCE -> lhs.isActive() && !rhs.isUniversal();
		};
	}
	
	/**
	 * @return a pessimistic assumption as to whether this message set has every
	 *         message.
	 */
	@Override
	public boolean isUniversal() {
		return switch (operator) {
			case UNION -> lhs.isUniversal() || rhs.isUniversal();
			case INTERSECTION -> lhs.isUniversal() && rhs.isUniversal();
			case DIFFERENCE -> lhs.isUniversal() && !rhs.isActive();
		};
	}	
}