/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.plugin;

import org.eclipse.xtext.generator.IGenerator2;

/**
 * Interface of RoboCert generator plugins.
 *
 * <p>
 * These have the usual structure of an Xtext generator, but with additional
 * metadata used in forming output configurations.
 *
 * @author Matt Windsor
 */
public interface RoboCertGeneratorPlugin extends IGenerator2 {
    /**
     * Gets a machine-readable ID for the generator.
     *
     * <p>The main generator will register the plugin's output configuration
     * under this ID.
     *
     * @return the ID.
     */
    String ID();

    /**
     * Gets a human-readable description for the generator.
     *
     * @return the description.
     */
    String description();
}
