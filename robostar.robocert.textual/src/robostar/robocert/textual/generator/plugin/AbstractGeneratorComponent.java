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

import java.util.Objects;

import org.eclipse.xtext.generator.IFileSystemAccess2;

/**
 * Boilerplate for subcomponents of a generator plugin.
 *
 * <p>This is the lowest common denominator between package generators and
 * standard library generators.
 *
 * @param <T> type of input to the generator.
 * @author Matt Windsor
 */
public abstract class AbstractGeneratorComponent<T> implements GeneratorComponent<T> {

    protected String outDir = "lib";
    protected String outputCfg = IFileSystemAccess2.DEFAULT_OUTPUT;

    /**
     * Changes the output directory from default.
     *
     * @param dir new output directory.
     */
    public void setOutputDirectory(String dir) {
        outDir = Objects.requireNonNull(dir);
    }

    /**
     * Changes the output configuration from default.
     *
     * @param id name of the new output configuration.
     */
    public void setOutputConfiguration(String id) {
        outputCfg = Objects.requireNonNull(id);
    }
    
    /**
     * Wrapper for {@code fsa.generateFile} that ensures the correct configuration.
     * @param fsa      the generator to use
     * @param fileName the file name to generate
     * @param contents the contents to write
     */
    protected void generateFile(IFileSystemAccess2 fsa, String fileName, CharSequence contents) {
    	var fqName = outDir.isEmpty() ? fileName : outDir + "/" + fileName;
    	
    	fsa.generateFile(fqName, outputCfg, contents);
    }
}
