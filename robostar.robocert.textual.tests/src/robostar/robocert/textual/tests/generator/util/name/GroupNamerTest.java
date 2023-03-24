/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.tests.generator.util.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import robostar.robocert.textual.generator.utils.name.GroupNamer;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/**
 * Tests {@link GroupNamer}.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class GroupNamerTest {
  @Inject private GroupNamer gn;
  @Inject private RoboCertFactory rf;

  /**
   * Tests that getting/synthesising the name of a group that already has a valid name is the
   * identity.
   */
  @Test
  void testGetOrSynthesiseName_namedGroup() {
    final var grp = rf.createSpecificationGroup();
    grp.setName("test");
    assertEquals("test", gn.getOrSynthesiseName(grp));
  }

  /**
   * Tests that getting/synthesising the name of a group that already has a valid name does some
   * collision avoidance..
   */
  @Test
  void testGetOrSynthesiseName_namedGroupCollision() {
    final var grp = rf.createSpecificationGroup();
    grp.setName(GroupNamer.PREFIX + "0_pkg");
    assertEquals(GroupNamer.PREFIX + "A_0_pkg", gn.getOrSynthesiseName(grp));
  }

  /**
   * Tests that synthesising the name of a group without either a name or a parent package returns
   * the synthesised name prefix alone.
   */
  @Test
  void testGetOrSynthesiseName_unnamedGroupNoPackage() {
    final var grp = rf.createSpecificationGroup();
    assertEquals(GroupNamer.PREFIX, gn.getOrSynthesiseName(grp));
  }

  /**
   * Tests that synthesising the name of a group with a parent package but no name takes into
   * account the package and the group's location in it.
   */
  @Test
  void testGetOrSynthesiseName_unnamedGroupInPackage() {
    final var pkg = rf.createCertPackage();
    // Not easy to test filename based naming here.
    pkg.setName("pkg");

    final var grp1 = rf.createSpecificationGroup();
    final var grp2 = rf.createAssertionGroup();
    pkg.getGroups().addAll(List.of(grp1, grp2));

    assertEquals(GroupNamer.PREFIX + "0_pkg", gn.getOrSynthesiseName(grp1));
    assertEquals(GroupNamer.PREFIX + "1_pkg", gn.getOrSynthesiseName(grp2));
  }
}
