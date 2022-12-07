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
 * Boilerplate for subcomponents of a generator plugin.
 *
 * <p>This is the lowest common denominator between package generators and
 * standard library generators.
 *
 * @param <T> type of input to the generator.
 * @author Matt Windsor
 */
public abstract class AbstractGeneratorComponent<T> {

    protected String outDir = "lib";
    protected String outputCfg = IFileSystemAccess2.DEFAULT_OUTPUT;

    /**
     * Generates this component of the plugin's RoboCert output.
     *
     * @param input   input for the generator.
     * @param fsa     file system access for writing files.
     * @param context context for the generator.
     */
    public abstract void generate(T input, IFileSystemAccess2 fsa, IGeneratorContext context);

    /**
     * Changes the output directory from default.
     *
     * @param dir new output directory.
     */
    public void setOutputDirectory(String dir) {
        outDir = dir;
    }

    /**
     * Changes the output configuration from default.
     *
     * @param outputCfg name of the new output configuration.
     */
    public void setOutputConfiguration(String outputCfg) {
        this.outputCfg = outputCfg;
    }
}
