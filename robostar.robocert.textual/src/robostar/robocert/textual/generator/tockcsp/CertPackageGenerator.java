/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp;

import com.google.inject.Inject;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.osgi.framework.FrameworkUtil;
import robostar.robocert.textual.generator.tockcsp.core.group.AssertionGroupGenerator;
import robostar.robocert.textual.generator.tockcsp.core.ImportGenerator;
import robostar.robocert.textual.generator.tockcsp.core.group.SpecificationGroupGenerator;
import robostar.robocert.textual.generator.utils.PackageGenerator;
import robostar.robocert.textual.generator.utils.name.GroupNamer;
import robostar.robocert.AssertionGroup;
import robostar.robocert.CSPGroup;
import robostar.robocert.CertPackage;
import robostar.robocert.Group;
import robostar.robocert.SpecificationGroup;

/**
 * Generates CSP-M for {@link CertPackage}s.
 *
 * @author Matt Windsor
 */
public record CertPackageGenerator(
    AssertionGroupGenerator ag,
    GroupNamer groupNamer,
    SpecificationGroupGenerator sg,
    ImportGenerator ig
) implements PackageGenerator {

  /**
   * Constructs a CertPackage generator.
   *
   * @param ag         the assertion group generator.
   * @param groupNamer a namer used for CSP groups.
   * @param sg         the sequence group generator.
   * @param ig         the import generator.
   */
  @Inject
  public CertPackageGenerator {
    Objects.requireNonNull(ag);
    Objects.requireNonNull(groupNamer);
    Objects.requireNonNull(sg);
    Objects.requireNonNull(ig);
  }

  @Override
  public void generate(IFileSystemAccess2 fsa, IGeneratorContext context, CertPackage pkg) {
    fsa.generateFile(groupNamer.getPackageName(pkg) + ".csp", generate(pkg));
  }

  /**
   * @param pkg the package being generated.
   * @return generated CSP for all elements.
   */
  public CharSequence generate(CertPackage pkg) {
    final var header = generateHeader();
    final var imports = ig.generate(pkg.eResource());
    final var groups = generateGroups(pkg);
    return String.join("\n\n", header, imports, groups);
  }

  /**
   * Similar to CUntimedGenerator in RoboChart.
   *
   * @return the generated header.
   */
  private CharSequence generateHeader() {
    final var timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    return """
        --- generated by RoboCert CSP generator version %s
        --- on %s""".formatted(version(), timestamp);
  }

  private String version() {
    try {
      return FrameworkUtil.getBundle(getClass()).getVersion().toString();
    } catch (Exception e) {
      return "<unknown>";
    }
  }

  /**
   * @param pkg the top-level package.
   * @return included CSP for all groups.
   */
  private String generateGroups(CertPackage pkg) {
    return pkg.getGroups().stream().map(this::generateGroup).collect(Collectors.joining("\n\n"));
  }

  private CharSequence generateGroup(Group it) {
    // TODO(@MattWindsor91): dependency-inject these somehow
    if (it instanceof AssertionGroup a) {
      return ag.generate(a);
    }
    if (it instanceof CSPGroup c) {
      return generateCSPGroup(c);
    }
    if (it instanceof SpecificationGroup s) {
      return sg.generate(s);
    }

    throw new IllegalArgumentException("unsupported group: %s".formatted(it));
  }

  private CharSequence generateCSPGroup(CSPGroup it) {
    final var name = groupNamer.getOrSynthesiseName(it);
    return String.join(
        "\n",
        "-- BEGIN INLINE CSP %s".formatted(name),
        it.getCsp(),
        "-- END INLINE CSP %s".formatted(name)
    );
  }
}
