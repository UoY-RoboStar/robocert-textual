/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.intf.seq.context;

import robostar.robocert.Interaction;

import java.util.stream.Stream;

/**
 * Common-denominator interface for interaction contexts.
 *
 * @author Matt Windsor
 */
public interface InteractionContext {
    /**
     * Gets the interaction for which this is a context.
     *
     * @return the interaction.
     */
    Interaction interaction();

    /**
     * Produces contexts for each of the lifelines in this interaction.
     *
     * @return a stream of lifeline contexts.
     */
    Stream<? extends LifelineContext> lifelines();
}
