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

package robostar.robocert.textual.generator.tikz;


import com.google.common.collect.Streams;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.Actor;
import robostar.robocert.CertPackage;
import robostar.robocert.Interaction;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.World;
import robostar.robocert.textual.generator.RoboCertOutputConfigurationProvider;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.Entry;
import robostar.robocert.textual.generator.tikz.InteractionUnwinder.EntryType;
import robostar.robocert.textual.generator.utils.PackageGenerator;
import robostar.robocert.textual.generator.utils.name.GroupNamer;

/**
 * Generates TikZ diagrams for a single {@link CertPackage}.
 *
 * @param gn synthesises names for CertPackages.
 *
 * @author Matt Windsor
 */
public record CertPackageGenerator(GroupNamer gn) implements PackageGenerator {
  /**
   * Constructs a tock-CSP generator.
   *
   * @param gn synthesises names for CertPackages.
   */
  @Inject
  public CertPackageGenerator {
    Objects.requireNonNull(gn);
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
            generate(diagram));
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

  private CharSequence generate(Interaction it) {
    // We treat the World separately -- it always appears at the end of a row.
    final var actors = it.getActors().stream().filter(x -> !(x instanceof World)).toList();

    final var unwound = new InteractionUnwinder(it).unwind();

    return matrix(actors, unwound);
  }

  private CharSequence matrix(List<Actor> actors, List<Entry> entries) {
    final var contents = entries.stream().map((e -> matrixRow(actors, e))).filter(Objects::nonNull)
        .collect(Collectors.joining("\n"));

    return """
        %% Remember to \\input or import the baseline definitions for RoboCert TikZ files.
        %% See the standalone .tex file for an example.
                
        %% Diagram grid:
        \\matrix[rcseq]{
        %s
        };""".formatted(contents);
  }

  private String matrixRow(List<Actor> actors, Entry entry) {
    final var cells = matrixRowCells(actors, entry);
    return cells == null ? null : cells.collect(Collectors.joining(" & ", "  ", " \\\\"));
  }

  private Stream<String> matrixRowCells(List<Actor> actors, Entry entry) {
    final var subject = entry.subject();
    final var type = entry.type();

    if (subject instanceof Interaction) {
      return diagramBoundaryRowCells(actors, type);
    }

    return null;
  }

  private Stream<String> diagramBoundaryRowCells(List<Actor> actors, EntryType type) {
    return Streams.concat(Stream.of(diagramBoundary(false, type)), actors.stream().map(_a -> ""),
        Stream.of(diagramBoundary(true, type)));
  }

  private String diagramBoundary(boolean isWorld, EntryType type) {
    final var actor = isWorld ? "w" : "b";
    return coordinate("diagram_%s_%s".formatted(actor, type.toString()));
  }

  private String coordinate(String name) {
    return "\\coordinate(%s);".formatted(name);
  }
}
