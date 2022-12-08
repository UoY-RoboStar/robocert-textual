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

import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

/**
 * An object that, given some input and Xtext generator context, can do part of
 * a plugin's generator duties.
 *
 * @param <T> type of input to this generator.
 */
public interface GeneratorComponent<T> {
    /**
     * Generates this component of the plugin's RoboCert output.
     *
     * @param input   input for the generator.
     * @param fsa     file system access for writing files.
     * @param context context for the generator.
     */
    void generate(T input, IFileSystemAccess2 fsa, IGeneratorContext context);
}
