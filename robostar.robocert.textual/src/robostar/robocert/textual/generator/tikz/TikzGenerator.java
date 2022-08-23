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


import com.google.inject.Inject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.Interaction;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.textual.generator.RoboCertOutputConfigurationProvider;
import robostar.robocert.textual.generator.utils.GeneratorUtil;
import robostar.robocert.textual.generator.utils.name.GroupNamer;

/**
 * Generates TikZ diagrams from RoboCert interaction diagrams.
 *
 * @author Matt Windsor
 */
public class TikzGenerator extends AbstractGenerator {

  private final GroupNamer gn;

  /**
   * Constructs a tock-CSP generator.
   *
   * @param gn synthesises names for CertPackages.
   */
  @Inject
  public TikzGenerator(GroupNamer gn) {
    super();

    this.gn = gn;
  }

  @Override
  public void doGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
    GeneratorUtil.forEachPackage(input, context, pkg -> {
      final var pkgName = gn.getPackageName(pkg);

      for (var cgroup : pkg.getGroups()) {
        if (!(cgroup instanceof SpecificationGroup group)) {
          continue;
        }

        final var groupName = gn.getOrSynthesiseName(group);

        for (var diagram : group.getInteractions()) {
          final var path = "diagrams/%s/%s/%s.tikz".formatted(pkgName, groupName,
              diagram.getName());
          fsa.generateFile(path, RoboCertOutputConfigurationProvider.TIKZ_OUTPUT,
              generate(diagram));
        }
      }
    });
  }


  private CharSequence generate(Interaction x) {
    return "% TODO";
  }
}
