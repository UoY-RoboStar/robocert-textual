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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

/**
 * Sub-generator for copying standard libraries for RoboCert output languages
 * into generated code.
 *
 * <p>This generator component takes as input the class whose resources should
 * be inspected for the library files.
 *
 * @author Matt Windsor
 */
public class StandardLibraryGenerator extends AbstractGeneratorComponent<Class<?>> {
    private String inDir = "lib/semantics";
    private final List<String> files = new ArrayList<>();

    /**
     * Generates files for each resource file in the 'standard library' for this output language.
     *
     * @param inClass class from which we are grabbing the library files.
     * @param fsa     filesystem access.
     * @param context cancellation context.
     */
    @Override
    public void generate(Class<?> inClass, IFileSystemAccess2 fsa, IGeneratorContext context) {
        Objects.requireNonNull(inClass, "Need a class from which to get resources");

        for (var filename : files) {
            if (context.getCancelIndicator().isCanceled()) {
                break;
            }

            final var inPath = String.join("/", inDir, filename);
            final var outPath = String.join("/", outDir, filename);

            final var stream = inClass.getResourceAsStream(inPath);
            Objects.requireNonNull(stream, () -> "Missing internal resource: " + inPath);

            fsa.generateFile(outPath, outputCfg, stream);
        }
    }

    /**
     * Changes the input directory from default.
     *
     * @param dir new input directory.
     */
    public void setInputDirectory(String dir) {
        inDir = dir;
    }


    /**
     * Adds the given files to the list to be generated.
     *
     * @param files files to generate.
     */
    public void addFiles(String... files) {
        this.files.addAll(List.of(files));
    }
}
