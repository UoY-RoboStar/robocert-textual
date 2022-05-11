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
package robocalc.robocert.tests.model;

import java.util.List;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.SetFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on ExtensionalMessageSets, and also tests that
 * the factory resolves it correctly.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class BinaryMessageSetImplCustomTest {
	@Inject
	protected RoboCertFactory rf;

	@Inject
	protected SetFactory sf;

	/**
	 * Tests to make sure isActive is handled correctly on unions.
	 */
	@Test
	void testIsActive_Union() {
		assertFalse(sf.union(sf.empty(), sf.empty()).isActive());
		assertTrue(sf.union(sf.universe(), sf.empty()).isActive());
		assertTrue(sf.union(sf.empty(), sf.universe()).isActive());
		assertTrue(sf.union(sf.universe(), sf.universe()).isActive());
		assertTrue(sf.union(single(), sf.empty()).isActive());
		assertTrue(sf.union(sf.empty(), single()).isActive());
		assertTrue(sf.union(single(), single()).isActive());
		assertTrue(sf.union(single(), sf.universe()).isActive());
		assertTrue(sf.union(sf.universe(), single()).isActive());
	}

	/**
	 * Tests to make sure isActive is handled correctly on intersections.
	 */
	@Test
	void testIsActive_Intersection() {
		assertFalse(sf.inter(sf.empty(), sf.empty()).isActive());
		assertFalse(sf.inter(sf.universe(), sf.empty()).isActive());
		assertFalse(sf.inter(sf.empty(), sf.universe()).isActive());
		assertTrue(sf.inter(sf.universe(), sf.universe()).isActive());
		assertFalse(sf.inter(single(), sf.empty()).isActive());
		assertFalse(sf.inter(sf.empty(), single()).isActive());
		assertTrue(sf.inter(single(), sf.universe()).isActive());
		assertTrue(sf.inter(sf.universe(), single()).isActive());
	}

	/**
	 * Tests to make sure isActive is handled correctly on differences.
	 */
	@Test
	void testIsActive_Difference() {
		assertFalse(sf.diff(sf.empty(), sf.empty()).isActive());
		assertTrue(sf.diff(sf.universe(), sf.empty()).isActive());
		assertFalse(sf.diff(sf.empty(), sf.universe()).isActive());
		assertFalse(sf.diff(sf.universe(), sf.universe()).isActive());
		assertTrue(sf.diff(single(), sf.empty()).isActive());
		assertFalse(sf.diff(sf.empty(), single()).isActive());
		assertFalse(sf.diff(single(), sf.universe()).isActive());
		assertTrue(sf.diff(sf.universe(), single()).isActive());
	}

	/**
	 * Tests to make sure isUniversal is handled correctly on unions.
	 */
	@Test
	void testIsUniversal_Union() {
		assertTrue(sf.union(sf.universe(), sf.empty()).isUniversal());
		assertTrue(sf.union(sf.empty(), sf.universe()).isUniversal());
		assertTrue(sf.union(sf.universe(), sf.universe()).isUniversal());
		assertTrue(sf.union(single(), sf.universe()).isUniversal());
		assertTrue(sf.union(sf.universe(), single()).isUniversal());
		assertFalse(sf.union(sf.empty(), sf.empty()).isUniversal());
		assertFalse(sf.union(single(), sf.empty()).isUniversal());
		assertFalse(sf.union(sf.empty(), single()).isUniversal());
		assertFalse(sf.union(single(), single()).isUniversal());
	}

	/**
	 * Tests to make sure isUniversal is handled correctly on intersections.
	 */
	@Test
	void testIsUniversal_Intersection() {
		assertTrue(sf.inter(sf.universe(), sf.universe()).isUniversal());
		assertFalse(sf.inter(sf.empty(), sf.empty()).isUniversal());
		assertFalse(sf.inter(sf.empty(), single()).isUniversal());
		assertFalse(sf.inter(sf.empty(), sf.universe()).isUniversal());
		assertFalse(sf.inter(single(), sf.empty()).isUniversal());
		assertFalse(sf.inter(single(), single()).isUniversal());
		assertFalse(sf.inter(single(), sf.universe()).isUniversal());
		assertFalse(sf.inter(sf.universe(), sf.empty()).isUniversal());
		assertFalse(sf.inter(sf.universe(), single()).isUniversal());
	}

	/**
	 * Tests to make sure isActive is handled correctly on differences.
	 */
	@Test
	void testIsUniversal_Difference() {
		assertTrue(sf.diff(sf.universe(), sf.empty()).isUniversal());
		assertFalse(sf.diff(sf.empty(), sf.empty()).isUniversal());
		assertFalse(sf.diff(sf.empty(), sf.universe()).isUniversal());
		assertFalse(sf.diff(sf.empty(), single()).isUniversal());
		assertFalse(sf.diff(single(), sf.empty()).isUniversal());
		assertFalse(sf.diff(single(), sf.universe()).isUniversal());
		assertFalse(sf.diff(single(), single()).isUniversal());
		assertFalse(sf.diff(sf.universe(), single()).isUniversal());
		assertFalse(sf.diff(sf.universe(), sf.universe()).isUniversal());
	}

	private ExtensionalMessageSet single() {
		return sf.extensional(List.of(rf.createMessage()));
	}
}
