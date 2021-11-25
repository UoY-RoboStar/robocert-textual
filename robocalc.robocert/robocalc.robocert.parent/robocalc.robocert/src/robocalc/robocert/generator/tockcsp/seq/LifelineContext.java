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
package robocalc.robocert.generator.tockcsp.seq;

import robocalc.robocert.model.robocert.Actor;

/**
 * A context used for a particular lifeline generation.
 * 
 * @author Matt Windsor
 */
public record LifelineContext(Actor actor, CharSequence alphabetCSP) {

}
