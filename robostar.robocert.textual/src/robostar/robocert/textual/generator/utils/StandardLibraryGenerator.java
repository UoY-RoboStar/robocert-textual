/*
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
 */

package robostar.robocert.textual.generator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

/**
 * Sub-generator for copying standard libraries for RoboCert output languages into generated code.
 *
 * @author Matt Windsor
 */
public class StandardLibraryGenerator {

  private String dir = "lib/semantics";	
  private String outputCfg = IFileSystemAccess2.DEFAULT_OUTPUT;
  private final List<String> files = new ArrayList<>();

  /**
   * Generates files for each resource file in the 'standard library' for this output language.
   *
   * @param fsa     filesystem access.
   * @param context cancellation context.
   * @param tClass  class from which we are grabbing the library files.
   * @param <T>     type of tClass.
   * @return true if we were interrupted at any point; false otherwise.
   */
  public <T> boolean generate(IFileSystemAccess2 fsa, IGeneratorContext context, Class<T> tClass) {
    for (var filename : files) {
      final var path = String.join("/",  dir, filename);
      final var stream = tClass.getResourceAsStream(path);
      Objects.requireNonNull(stream, () -> "Missing internal resource: " + path);

      fsa.generateFile(filename, outputCfg, stream);
      if (context.getCancelIndicator().isCanceled()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Changes the input directory from default.
   *
   * @param dir new input directory.
   */
  public void setInputDirectory(String dir) {
    this.dir = dir;
  }

  /**
   * Changes the output configuration from default.
   *
   * @param outputCfg name of the new output configuration.
   */
  public void setOutputConfiguration(String outputCfg) {
    this.outputCfg = outputCfg;
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
