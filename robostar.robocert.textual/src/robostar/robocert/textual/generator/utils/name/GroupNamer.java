/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robostar.robocert.textual.generator.utils.name;

import com.google.common.io.Files;
import java.util.Optional;
import org.eclipse.emf.common.util.EList;
import robostar.robocert.CertPackage;
import robostar.robocert.Group;

/**
 * Determines a hopefully-unambigous name for {@link Group}s.
 *
 * <p>This class is needed because we often need to generate CSP-M modules for groups, but not all
 * groups have names.
 *
 * @author Matt Windsor
 */
public class GroupNamer extends OptionalNamer<Group> {
  /** The prefix appended to synthesised names, exposed for testing purposes. */
  public static final String PREFIX = "Untitled_Group__";

  @Override
  protected String getPrefix() {
    return PREFIX;
  }

  @Override
  protected EList<Group> getContainer(Group it) {
    final var pkg = it.getParent();
    return pkg == null ? null : pkg.getGroups();
  }

  @Override
  protected String getContainerName(Group it) {
    final var pkg = it.getParent();
    return pkg == null ? null : getPackageName(pkg);
  }

  /**
   * Deduces a name for a {@link CertPackage}.
   *
   * <p>If none is given, a sensible name will be synthesised from the resource filename.
   *
   * @param pkg the package to name.
   * @return the deduced name.
   */
  public String getPackageName(CertPackage pkg) {
    // TODO(@MattWindsor91): displaced from FilenameExtensions; where can it go?
    // Similar to getFileName from GeneratorUtils.
    //noinspection UnstableApiUsage
    return Optional.ofNullable(pkg.getName())
        .map(x -> x.replaceAll("::", "_"))
        .orElseGet(() -> Files.getNameWithoutExtension(pkg.eResource().getURI().lastSegment()));
  }
}
