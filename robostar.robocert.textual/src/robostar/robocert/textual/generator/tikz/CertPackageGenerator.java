/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz;


import java.util.Objects;

import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

import com.google.inject.Inject;

import robostar.robocert.CertPackage;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.textual.generator.RoboCertOutputConfigurationProvider;
import robostar.robocert.textual.generator.tikz.diagram.DiagramGenerator;
import robostar.robocert.textual.generator.utils.PackageGenerator;
import robostar.robocert.textual.generator.utils.name.GroupNamer;

/**
 * Generates TikZ diagrams for a single {@link CertPackage}.
 *
 * @param gn   synthesises names for CertPackages.
 * @param dGen generates TikZ for diagrams.
 * @author Matt Windsor
 */
public record CertPackageGenerator(GroupNamer gn, DiagramGenerator dGen) implements
    PackageGenerator {

  /**
   * Constructs a tock-CSP generator.
   *
   * @param gn synthesises names for CertPackages.
   */
  @Inject
  public CertPackageGenerator {
    Objects.requireNonNull(gn);
    Objects.requireNonNull(dGen);
  }

  @Override
  public void generate(IFileSystemAccess2 fsa, IGeneratorContext context, CertPackage pkg) {
    final var pkgName = gn.getPackageName(pkg);

    for (var cgroup : pkg.getGroups()) {
      if (!(cgroup instanceof SpecificationGroup group)) {
        continue;
      }

      final var groupName = gn.getOrSynthesiseName(group);

      for (var diagram : group.getInteractions()) {
        final var diagramName = diagram.getName();

        final var stem = "%s/%s/%s".formatted(pkgName, groupName, diagramName);

        fsa.generateFile(stem + ".tikz", RoboCertOutputConfigurationProvider.TIKZ_OUTPUT,
            dGen.generate(diagram));
        fsa.generateFile(stem + ".tex", RoboCertOutputConfigurationProvider.TIKZ_OUTPUT,
            standalone(diagramName));
      }
    }
  }

  /**
   * Emits a standalone diagram harness.
   *
   * @param name name of the diagram TikZ file to include into the document.
   * @return a standalone diagram harness, in LaTeX.
   */
  private CharSequence standalone(String name) {
    return """
        \\documentclass[tikz]{standalone}
        \\usepackage{xparse}
        \\input{../../../lib/defs}
              
        \\begin{document}
          \\begin{tikzpicture}
            \\input{%s.tikz}
          \\end{tikzpicture}
        \\end{document}""".formatted(name);
  }
}
